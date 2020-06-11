/*
 *   Copyright 2020 Open LVC Project.
 *
 *   This file is part of Open LVC Disco.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.openlvc.disco.bus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.openlvc.disco.DiscoException;

/**
 * Very basic publish/subscribe message bus.
 * <p/>
 * 
 * To receive messages just add the {@link EventHandler} annotation to any method in your class
 * and then register an instance of that class with a bus via {@link #subscribe(Object)}. The
 * bus will pick out all the tagged methods and subscribe them to messages that are of the same
 * type as their first argument. <b>NOTE:</b> The methods must only take a single argument.
 * <p/>
 * 
 * <b>Thread Safety</b><p/>
 * Subscribe/Unsubscribe methods and calls to {@link #publish(Object)} are thread safe.
 * <p/>
 * 
 * <b>Synchronous v. Asynchronous</b><p/>
 * The bus currently only supports synchronous calls.
 * <p/>
 * 
 * <b>Inheritance<b><p/>
 * Any message send to the bus via {@link #publish(Object)} will be passed to all subscribers for
 * that type. The bus will then pass the message to all subscribers for the types parent class, and
 * so on all the way up to <code>Object</code> (Object will not be processed).
 * 
 * <b>Error Handlers</b><p/>
 * Subscribers can have methods that register the {@link ErrorHandler} annotation. These methods
 * will be passed any errors that get generated when an event is called. Error handlers must declare
 * two parameters (throwable, for the error; and object, which will be populated with the target
 * object).
 * <p/>
 * 
 * @param <T> The type of messages you want to send to the bus via {@link #publish(Object)}
 */
public class MessageBus<T>
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private ConcurrentHashMap<Class<?>,List<CallablePair>> subscribers;
	private List<CallablePair> errorHandlers;

	// Configuration Options
	private boolean throwExceptionOnError;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public MessageBus()
	{
		this.subscribers = new ConcurrentHashMap<>();
		this.errorHandlers = new CopyOnWriteArrayList<>();
		
		// Configuration Options
		this.throwExceptionOnError = false;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Message Publication Methods   //////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Publish the given message to all subscribers. This method will find all subscribers for
	 * the class of the message and invoke them. It will then loop up to the parent class and
	 * repeat (up to, but not including, Object).
	 * <p/>
	 * This method will block until the message has been passed to all subscribers.
	 * 
	 * @param message The message to send to subscribers
	 */
	public void publish( T message ) throws DiscoException
	{
		Class<?> type = message.getClass();
		do
		{
			safeGet(type).forEach( subscriber -> subscriber.call(message) );
			type = type.getSuperclass();
		}
		while( type != null && type != Object.class );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Subscription Management   //////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void subscribe( Object... subscribers )
	{
		for( Object subscriber : subscribers )
			subscribe(subscriber);
	}
	
	public void subscribe( Object subscriber )
	{
		// Find all declared methods with the @EventHandler annotation
		for( Method method : subscriber.getClass().getDeclaredMethods() )
		{
			// If this is a valid @EventHandler, add it to the store of event handlers
			if( isEventHandler(method) )
			{
				CallablePair pair = new CallablePair( subscriber, method );
				List<CallablePair> list = safeGet(pair.type);
				if( list.contains(pair) == false )
					list.add( pair );
			}

			// If this is a valid @ErrorHandler, add it to the store of error handlers
			if( isErrorHandler(method) )
			{
				CallablePair pair = new CallablePair( subscriber, method );
				if( errorHandlers.contains(pair) == false )
					errorHandlers.add( pair );
			}
		}
	}

	/**
	 * Return true if the given method is a valid {@link EventHandler}. To be this it must:
	 * <ul>
	 *   <li>Declare the {@link EventHandler} annotation</li>
	 *   <li>Not a static method</li>
	 *   <li>Must be public</li>
	 *   <li>Take a single parameter</li>
	 * </ul>
	 * 
	 * @param method The method to assess
	 * @return True if the method is a valid event handler, falst otherwise
	 * @throws DiscoException If the method declared {@link EventHandler}, but doesn't conform
	 *                        to the other required rules for an event handler. Something is wrong
	 *                        in the definition, so we should flag that.
	 */
	private boolean isEventHandler( Method method ) throws DiscoException
	{
		// Does it declare the @EventHandler annotation?
		if( method.getDeclaredAnnotation(EventHandler.class) == null )
			return false;
		
		// Is the method public?
		if( Modifier.isPublic(method.getModifiers()) == false )
			throw new DiscoException( "@EventHandler methods must be public; found: "+method.toGenericString() );
		
		if( Modifier.isStatic(method.getModifiers()) )
			throw new DiscoException( "@EventHandler methods cannot be static; found: "+method.toGenericString() );
		
		if( Modifier.isAbstract(method.getModifiers()) )
			throw new DiscoException( "@EventHandler methods cannot be abstract; found: "+method.toGenericString() );
		
		// Does it have the right number of parameters?
		if( method.getParameterCount() != 1 )
			throw new DiscoException( "@EventHandler methods must have one param; found: "+method.toGenericString() );
		
		return true;
	}
	
	private boolean isErrorHandler( Method method )
	{
		// Does it declare the @EventHandler annotation?
		if( method.getDeclaredAnnotation(ErrorHandler.class) == null )
			return false;
		
		// Is the method public?
		if( Modifier.isPublic(method.getModifiers()) )
			throw new DiscoException( "@ErrorHandler methods must be public; found: "+method.toGenericString() );
		
		if( Modifier.isStatic(method.getModifiers()) )
			throw new DiscoException( "@ErrorHandler methods cannot be static; found: "+method.toGenericString() );
		
		if( Modifier.isAbstract(method.getModifiers()) )
			throw new DiscoException( "@ErrorHandler methods cannot be abstract; found: "+method.toGenericString() );
		
		// Does it have the right number and type of parameters?
		if( method.getParameterCount() != 2 ||
		    method.getParameterTypes()[0] != Throwable.class ||
		    method.getParameterTypes()[1] != Object.class )
		{
			throw new DiscoException( "@ErrorHandler methods must signature (Throwable,Object); found: "+
			                          method.toGenericString() );
		}
		
		return true;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Return the list of subscribers for the given type. If there is no list for that type, one
	 * is created.
	 * 
	 * @param type The type to look up
	 * @return The list of subscribers for that explicit type.
	 */
	private List<CallablePair> safeGet( Class<?> type )
	{
		List<CallablePair> list = subscribers.get( type );
		if( list == null )
		{
			list = new CopyOnWriteArrayList<>();
			subscribers.put( type, list );
		}
		
		return list;
	}

	
	public boolean isThrowExceptionOnError()
	{
		return this.throwExceptionOnError;
	}
	
	public void setThrowExceptionOnError( boolean value )
	{
		this.throwExceptionOnError = value;
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Private Inner Class: CallablePair   ////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Links a methods carrying the {@link EventHandler} annotation with the object that it should
	 * be invoked on into one object nad provides a convenient invocation method for it.
	 */
	private class CallablePair
	{
		private Object target;
		private Method method;
		private Class<?> type;
		
		private CallablePair( Object target, Method method )
		{
			this.target = target;
			this.method = method;
			this.type = method.getParameterTypes()[0]; // Either MessageType, or Throwable for error handler
		}

		/**
		 * Invoke the stored method on the stored object, passing the given message object as
		 * the parameter (will get turned into whatever type is expected).
		 *  
		 * @param message The message object we are trying to publish
		 * @throws DiscoException Any exception the method throws gets wrapped in a DiscoException
		 */
		private void call( Object message ) throws DiscoException
		{
			try
			{
				this.method.invoke( target, message );
			}
			catch( InvocationTargetException ie )
			{
				errorHandlers.forEach( handler -> handler.error(ie.getCause(),target) );
				if( throwExceptionOnError )
					throw new DiscoException( ie.getMessage(), ie.getCause() );
			}
			catch( Throwable throwable )
			{
				errorHandlers.forEach( handler -> handler.error(throwable,target) );
				if( throwExceptionOnError )
					throw new DiscoException( throwable.getMessage(), throwable );
			}
		}

		/**
		 * To be called if this is an error handler.
		 * 
		 * @param cause  Exception that represents the error
		 * @param callee Object we invoked on when the error happened
		 */
		private void error( Throwable cause, Object callee )
		{
			try
			{
				this.method.invoke( target, cause, callee );
			}
			catch( Throwable throwable )
			{
				// NFI
				throwable.printStackTrace();
			}
		}

		@Override
		public int hashCode()
		{
			return target.hashCode();
		}
		
		@Override
		@SuppressWarnings({"rawtypes","unchecked"})
		public boolean equals( Object object )
		{
			if( object instanceof MessageBus.CallablePair )
			{
				CallablePair other = (MessageBus.CallablePair)object;
				return this.target == other.target &&
				       this.method == other.method;
			}
			
			return false;
		}
	}
	
	
}
