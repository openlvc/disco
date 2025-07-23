/*
 *   Copyright 2015 Open LVC Project.
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
package org.openlvc.disco.pdu.field;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.openlvc.disco.pdu.DisSizes;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.emissions.DesignatorPdu;
import org.openlvc.disco.pdu.emissions.EmissionPdu;
import org.openlvc.disco.pdu.entity.EntityStatePdu;
import org.openlvc.disco.pdu.minefield.MinefieldDataPdu;
import org.openlvc.disco.pdu.minefield.MinefieldStatePdu;
import org.openlvc.disco.pdu.radio.ReceiverPdu;
import org.openlvc.disco.pdu.radio.SignalPdu;
import org.openlvc.disco.pdu.radio.TransmitterPdu;
import org.openlvc.disco.pdu.simman.AcknowledgePdu;
import org.openlvc.disco.pdu.simman.ActionRequestPdu;
import org.openlvc.disco.pdu.simman.ActionResponsePdu;
import org.openlvc.disco.pdu.simman.CommentPdu;
import org.openlvc.disco.pdu.simman.DataPdu;
import org.openlvc.disco.pdu.simman.SetDataPdu;
import org.openlvc.disco.pdu.simman.StartResumePdu;
import org.openlvc.disco.pdu.simman.StopFreezePdu;
import org.openlvc.disco.pdu.synthenv.ArealObjectStatePdu;
import org.openlvc.disco.pdu.synthenv.LinearObjectStatePdu;
import org.openlvc.disco.pdu.warfare.DetonationPdu;
import org.openlvc.disco.pdu.warfare.FirePdu;
import org.openlvc.disco.utils.EnumLookup;

public enum PduType
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	Other                ( (short)0,   ProtocolFamily.Other ),
	EntityState          ( (short)1,   ProtocolFamily.Warfare,        EntityStatePdu.class ),
	Fire                 ( (short)2,   ProtocolFamily.Warfare,        FirePdu.class ),
	Detonation           ( (short)3,   ProtocolFamily.Warfare,        DetonationPdu.class ),
	Collision            ( (short)4,   ProtocolFamily.Entity ),
	ServiceRequest       ( (short)5,   ProtocolFamily.Logistics ),
	ResupplyOffer        ( (short)6,   ProtocolFamily.Logistics ),
	ResupplyReceived     ( (short)7,   ProtocolFamily.Logistics ),
	ResupplyCancel       ( (short)8,   ProtocolFamily.Logistics ),
	RepairComplete       ( (short)9,   ProtocolFamily.Logistics ),
	RepairResponse       ( (short)10,  ProtocolFamily.Logistics ),
 	
	CreateEntity         ( (short)11,  ProtocolFamily.SimMgmt ),
	RemoveEntity         ( (short)12,  ProtocolFamily.SimMgmt ),
	StartResume          ( (short)13,  ProtocolFamily.SimMgmt,        StartResumePdu.class ),
	StopFreeze           ( (short)14,  ProtocolFamily.SimMgmt,        StopFreezePdu.class ),
	Acknowledge          ( (short)15,  ProtocolFamily.SimMgmt ,       AcknowledgePdu.class ),
	ActionRequest        ( (short)16,  ProtocolFamily.SimMgmt,        ActionRequestPdu.class ),
	ActionResponse       ( (short)17,  ProtocolFamily.SimMgmt,        ActionResponsePdu.class ),
	DataQuery            ( (short)18,  ProtocolFamily.SimMgmt ),
	SetData              ( (short)19,  ProtocolFamily.SimMgmt,        SetDataPdu.class ),
	Data                 ( (short)20,  ProtocolFamily.SimMgmt,        DataPdu.class ),
	EventReport          ( (short)21,  ProtocolFamily.SimMgmt ),
	Comment              ( (short)22,  ProtocolFamily.SimMgmt,        CommentPdu.class ),
	
	Emission             ( (short)23,  ProtocolFamily.Emission,       EmissionPdu.class ),
	Designator           ( (short)24,  ProtocolFamily.Emission,       DesignatorPdu.class ),
	Transmitter          ( (short)25,  ProtocolFamily.Radio,          TransmitterPdu.class ),
	Signal               ( (short)26,  ProtocolFamily.Radio,          SignalPdu.class ),
	Receiver             ( (short)27,  ProtocolFamily.Radio,          ReceiverPdu.class ),
	IFF                  ( (short)28,  ProtocolFamily.Emission ),
	UnderwaterAcoustic   ( (short)29,  ProtocolFamily.Emission ),
	SupplementalEmmission( (short)30, ProtocolFamily.Emission ), // ?
	
	IntercomSignal       ( (short)31,  ProtocolFamily.Radio ),
	IntercomControl      ( (short)32,  ProtocolFamily.Radio ),
	
	AggregateSate        ( (short)33,  ProtocolFamily.EntityMgmt ),
	IsGroupOf            ( (short)34,  ProtocolFamily.EntityMgmt ),
	
	TransferOwnership    ( (short)35,  ProtocolFamily.EntityMgmt ),
	IsPartOf             ( (short)36,  ProtocolFamily.EntityMgmt ),
	
	MinefieldState       ( (short)37,  ProtocolFamily.Minefield,      MinefieldStatePdu.class ),
	MinefieldQuery       ( (short)38,  ProtocolFamily.Minefield ),
	MinefieldData        ( (short)39,  ProtocolFamily.Minefield,      MinefieldDataPdu.class ),
	MinefieldRspNACK     ( (short)40,  ProtocolFamily.Minefield ),
	
	EnvironmentalProc    ( (short)41,  ProtocolFamily.SyntheticEnv ),
	GriddedData          ( (short)42,  ProtocolFamily.SyntheticEnv ),
	PointObjectState     ( (short)43,  ProtocolFamily.SyntheticEnv ),
	LinearObjectState    ( (short)44,  ProtocolFamily.SyntheticEnv,   LinearObjectStatePdu.class ),
	ArealObjectState     ( (short)45,  ProtocolFamily.SyntheticEnv,   ArealObjectStatePdu.class ),
	
	TSPI                 ( (short)46,  ProtocolFamily.LiveEntity ),
	Appearance           ( (short)47,  ProtocolFamily.LiveEntity ),
	ArticulatedParts     ( (short)48,  ProtocolFamily.LiveEntity ),
	LEFire               ( (short)49,  ProtocolFamily.LiveEntity ),
	LEDetonation         ( (short)50,  ProtocolFamily.LiveEntity ),

	CreateEntity_R       ( (short)51,  ProtocolFamily.SimMgmt_R ),
	RemoveEntity_R       ( (short)52,  ProtocolFamily.SimMgmt_R ),
	StartResume_R        ( (short)53,  ProtocolFamily.SimMgmt_R ),
	StopFreeze_R         ( (short)54,  ProtocolFamily.SimMgmt_R ),
	Acknowledge_R        ( (short)55,  ProtocolFamily.SimMgmt_R ),
	ActionRequest_R      ( (short)56,  ProtocolFamily.SimMgmt_R ),
	ActionResponse_R     ( (short)57,  ProtocolFamily.SimMgmt_R ),
	DataQuery_R          ( (short)58,  ProtocolFamily.SimMgmt_R ),
	SetData_R            ( (short)59,  ProtocolFamily.SimMgmt_R ),
	Data_R               ( (short)60,  ProtocolFamily.SimMgmt_R ),
	EventReport_R        ( (short)61,  ProtocolFamily.SimMgmt_R ),
	Comment_R            ( (short)62,  ProtocolFamily.SimMgmt_R ),
	Record_R             ( (short)63,  ProtocolFamily.SimMgmt_R ),
	SetRecord_R          ( (short)64,  ProtocolFamily.SimMgmt_R ),
	RecordQuery_R        ( (short)65,  ProtocolFamily.SimMgmt_R ),

	CollisionElastic     ( (short)66,  ProtocolFamily.Entity ),
	EntityStateUpdate    ( (short)67,  ProtocolFamily.Entity ),
	DirectedEnergyFire   ( (short)68,  ProtocolFamily.Warfare ),
	EntityDamageStatus   ( (short)69,  ProtocolFamily.Warfare ),

	InfoOpsAction        ( (short)70,  ProtocolFamily.InformationOps ),
	InfoOpsReport        ( (short)71,  ProtocolFamily.InformationOps ),
	Attribute            ( (short)72,  ProtocolFamily.Entity ),
	
	// Custom Extensions
	Custom_73            ( (short)73,  ProtocolFamily.DiscoCustom ),
	Custom_74            ( (short)74,  ProtocolFamily.DiscoCustom ),
	Custom_75            ( (short)75,  ProtocolFamily.DiscoCustom ),
	Custom_76            ( (short)76,  ProtocolFamily.DiscoCustom ),
	Custom_77            ( (short)77,  ProtocolFamily.DiscoCustom ),
	Custom_78            ( (short)78,  ProtocolFamily.DiscoCustom ),
	Custom_79            ( (short)79,  ProtocolFamily.DiscoCustom ),
	// 80-89
	Custom_80            ( (short)80,  ProtocolFamily.DiscoCustom ),
	Custom_81            ( (short)81,  ProtocolFamily.DiscoCustom ),
	Custom_82            ( (short)82,  ProtocolFamily.DiscoCustom ),
	Custom_83            ( (short)83,  ProtocolFamily.DiscoCustom ),
	Custom_84            ( (short)84,  ProtocolFamily.DiscoCustom ),
	Custom_85            ( (short)85,  ProtocolFamily.DiscoCustom ),
	Custom_86            ( (short)86,  ProtocolFamily.DiscoCustom ),
	Custom_87            ( (short)87,  ProtocolFamily.DiscoCustom ),
	Custom_88            ( (short)88,  ProtocolFamily.DiscoCustom ),
	Custom_89            ( (short)89,  ProtocolFamily.DiscoCustom ),
	// 90-99
	Custom_90            ( (short)90,  ProtocolFamily.DiscoCustom ),
	Custom_91            ( (short)91,  ProtocolFamily.DiscoCustom ),
	Custom_92            ( (short)92,  ProtocolFamily.DiscoCustom ),
	Custom_93            ( (short)93,  ProtocolFamily.DiscoCustom ),
	Custom_94            ( (short)94,  ProtocolFamily.DiscoCustom ),
	Custom_95            ( (short)95,  ProtocolFamily.DiscoCustom ),
	Custom_96            ( (short)96,  ProtocolFamily.DiscoCustom ),
	Custom_97            ( (short)97,  ProtocolFamily.DiscoCustom ),
	Custom_98            ( (short)98,  ProtocolFamily.DiscoCustom ),
	Custom_99            ( (short)99,  ProtocolFamily.DiscoCustom ),
	// 100-109
	Custom_100           ( (short)100, ProtocolFamily.DiscoCustom ),
	Custom_101           ( (short)101, ProtocolFamily.DiscoCustom ),
	Custom_102           ( (short)102, ProtocolFamily.DiscoCustom ),
	Custom_103           ( (short)103, ProtocolFamily.DiscoCustom ),
	Custom_104           ( (short)104, ProtocolFamily.DiscoCustom ),
	Custom_105           ( (short)105, ProtocolFamily.DiscoCustom ),
	Custom_106           ( (short)106, ProtocolFamily.DiscoCustom ),
	Custom_107           ( (short)107, ProtocolFamily.DiscoCustom ),
	Custom_108           ( (short)108, ProtocolFamily.DiscoCustom ),
	Custom_109           ( (short)109, ProtocolFamily.DiscoCustom ),
	// 110-119
	Custom_110           ( (short)110, ProtocolFamily.DiscoCustom ),
	Custom_111           ( (short)111, ProtocolFamily.DiscoCustom ),
	Custom_112           ( (short)112, ProtocolFamily.DiscoCustom ),
	Custom_113           ( (short)113, ProtocolFamily.DiscoCustom ),
	Custom_114           ( (short)114, ProtocolFamily.DiscoCustom ),
	Custom_115           ( (short)115, ProtocolFamily.DiscoCustom ),
	Custom_116           ( (short)116, ProtocolFamily.DiscoCustom ),
	Custom_117           ( (short)117, ProtocolFamily.DiscoCustom ),
	Custom_118           ( (short)118, ProtocolFamily.DiscoCustom ),
	Custom_119           ( (short)119, ProtocolFamily.DiscoCustom ),
	// 120-129
	Custom_120           ( (short)120, ProtocolFamily.DiscoCustom ),
	Custom_121           ( (short)121, ProtocolFamily.DiscoCustom ),
	Custom_122           ( (short)122, ProtocolFamily.DiscoCustom ),
	Custom_123           ( (short)123, ProtocolFamily.DiscoCustom ),
	Custom_124           ( (short)124, ProtocolFamily.DiscoCustom ),
	Custom_125           ( (short)125, ProtocolFamily.DiscoCustom ),
	Custom_126           ( (short)126, ProtocolFamily.DiscoCustom ),
	Custom_127           ( (short)127, ProtocolFamily.DiscoCustom ),
	Custom_128           ( (short)128, ProtocolFamily.DiscoCustom ),
	Custom_129           ( (short)129, ProtocolFamily.DiscoCustom ),
	// 130-139
	Custom_130           ( (short)130, ProtocolFamily.DiscoCustom ),
	Custom_131           ( (short)131, ProtocolFamily.DiscoCustom ),
	Custom_132           ( (short)132, ProtocolFamily.DiscoCustom ),
	Custom_133           ( (short)133, ProtocolFamily.DiscoCustom ),
	Custom_134           ( (short)134, ProtocolFamily.DiscoCustom ),
	Custom_135           ( (short)135, ProtocolFamily.DiscoCustom ),
	Custom_136           ( (short)136, ProtocolFamily.DiscoCustom ),
	Custom_137           ( (short)137, ProtocolFamily.DiscoCustom ),
	Custom_138           ( (short)138, ProtocolFamily.DiscoCustom ),
	Custom_139           ( (short)139, ProtocolFamily.DiscoCustom ),
	// 140-149
	Custom_140           ( (short)140, ProtocolFamily.DiscoCustom ),
	Custom_141           ( (short)141, ProtocolFamily.DiscoCustom ),
	Custom_142           ( (short)142, ProtocolFamily.DiscoCustom ),
	Custom_143           ( (short)143, ProtocolFamily.DiscoCustom ),
	Custom_144           ( (short)144, ProtocolFamily.DiscoCustom ),
	Custom_145           ( (short)145, ProtocolFamily.DiscoCustom ),
	Custom_146           ( (short)146, ProtocolFamily.DiscoCustom ),
	Custom_147           ( (short)147, ProtocolFamily.DiscoCustom ),
	Custom_148           ( (short)148, ProtocolFamily.DiscoCustom ),
	Custom_149           ( (short)149, ProtocolFamily.DiscoCustom ),
	// 150-159
	Custom_150           ( (short)150, ProtocolFamily.DiscoCustom ),
	Custom_151           ( (short)151, ProtocolFamily.DiscoCustom ),
	Custom_152           ( (short)152, ProtocolFamily.DiscoCustom ),
	Custom_153           ( (short)153, ProtocolFamily.DiscoCustom ),
	Custom_154           ( (short)154, ProtocolFamily.DiscoCustom ),
	Custom_155           ( (short)155, ProtocolFamily.DiscoCustom ),
	Custom_156           ( (short)156, ProtocolFamily.DiscoCustom ),
	Custom_157           ( (short)157, ProtocolFamily.DiscoCustom ),
	Custom_158           ( (short)158, ProtocolFamily.DiscoCustom ),
	Custom_159           ( (short)159, ProtocolFamily.DiscoCustom ),
	// 160-169
	Custom_160           ( (short)160, ProtocolFamily.DiscoCustom ),
	Custom_161           ( (short)161, ProtocolFamily.DiscoCustom ),
	Custom_162           ( (short)162, ProtocolFamily.DiscoCustom ),
	Custom_163           ( (short)163, ProtocolFamily.DiscoCustom ),
	Custom_164           ( (short)164, ProtocolFamily.DiscoCustom ),
	Custom_165           ( (short)165, ProtocolFamily.DiscoCustom ),
	Custom_166           ( (short)166, ProtocolFamily.DiscoCustom ),
	Custom_167           ( (short)167, ProtocolFamily.DiscoCustom ),
	Custom_168           ( (short)168, ProtocolFamily.DiscoCustom ),
	Custom_169           ( (short)169, ProtocolFamily.DiscoCustom ),
	// 170-179
	Custom_170           ( (short)170, ProtocolFamily.DiscoCustom ),
	Custom_171           ( (short)171, ProtocolFamily.DiscoCustom ),
	Custom_172           ( (short)172, ProtocolFamily.DiscoCustom ),
	Custom_173           ( (short)173, ProtocolFamily.DiscoCustom ),
	Custom_174           ( (short)174, ProtocolFamily.DiscoCustom ),
	Custom_175           ( (short)175, ProtocolFamily.DiscoCustom ),
	Custom_176           ( (short)176, ProtocolFamily.DiscoCustom ),
	Custom_177           ( (short)177, ProtocolFamily.DiscoCustom ),
	Custom_178           ( (short)178, ProtocolFamily.DiscoCustom ),
	Custom_179           ( (short)179, ProtocolFamily.DiscoCustom ),
	// 180-189
	Custom_180           ( (short)180, ProtocolFamily.DiscoCustom ),
	Custom_181           ( (short)181, ProtocolFamily.DiscoCustom ),
	Custom_182           ( (short)182, ProtocolFamily.DiscoCustom ),
	Custom_183           ( (short)183, ProtocolFamily.DiscoCustom ),
	Custom_184           ( (short)184, ProtocolFamily.DiscoCustom ),
	Custom_185           ( (short)185, ProtocolFamily.DiscoCustom ),
	Custom_186           ( (short)186, ProtocolFamily.DiscoCustom ),
	Custom_187           ( (short)187, ProtocolFamily.DiscoCustom ),
	Custom_188           ( (short)188, ProtocolFamily.DiscoCustom ),
	Custom_189           ( (short)189, ProtocolFamily.DiscoCustom ),
	// 190-199
	Custom_190           ( (short)190, ProtocolFamily.DiscoCustom ),
	Custom_191           ( (short)191, ProtocolFamily.DiscoCustom ),
	Custom_192           ( (short)192, ProtocolFamily.DiscoCustom ),
	Custom_193           ( (short)193, ProtocolFamily.DiscoCustom ),
	Custom_194           ( (short)194, ProtocolFamily.DiscoCustom ),
	Custom_195           ( (short)195, ProtocolFamily.DiscoCustom ),
	Custom_196           ( (short)196, ProtocolFamily.DiscoCustom ),
	Custom_197           ( (short)197, ProtocolFamily.DiscoCustom ),
	Custom_198           ( (short)198, ProtocolFamily.DiscoCustom ),
	Custom_199           ( (short)199, ProtocolFamily.DiscoCustom ),
	// 200-209
	Custom_200           ( (short)200, ProtocolFamily.DiscoCustom ),
	Custom_201           ( (short)201, ProtocolFamily.DiscoCustom ),
	Custom_202           ( (short)202, ProtocolFamily.DiscoCustom ),
	Custom_203           ( (short)203, ProtocolFamily.DiscoCustom ),
	Custom_204           ( (short)204, ProtocolFamily.DiscoCustom ),
	Custom_205           ( (short)205, ProtocolFamily.DiscoCustom ),
	Custom_206           ( (short)206, ProtocolFamily.DiscoCustom ),
	Custom_207           ( (short)207, ProtocolFamily.DiscoCustom ),
	Custom_208           ( (short)208, ProtocolFamily.DiscoCustom ),
	Custom_209           ( (short)209, ProtocolFamily.DiscoCustom ),
	// 210-219
	Custom_210           ( (short)210, ProtocolFamily.DiscoCustom ),
	Custom_211           ( (short)211, ProtocolFamily.DiscoCustom ),
	Custom_212           ( (short)212, ProtocolFamily.DiscoCustom ),
	Custom_213           ( (short)213, ProtocolFamily.DiscoCustom ),
	Custom_214           ( (short)214, ProtocolFamily.DiscoCustom ),
	Custom_215           ( (short)215, ProtocolFamily.DiscoCustom ),
	Custom_216           ( (short)216, ProtocolFamily.DiscoCustom ),
	Custom_217           ( (short)217, ProtocolFamily.DiscoCustom ),
	Custom_218           ( (short)218, ProtocolFamily.DiscoCustom ),
	Custom_219           ( (short)219, ProtocolFamily.DiscoCustom ),
	// 220-229
	Custom_220           ( (short)220, ProtocolFamily.DiscoCustom ),
	Custom_221           ( (short)221, ProtocolFamily.DiscoCustom ),
	Custom_222           ( (short)222, ProtocolFamily.DiscoCustom ),
	Custom_223           ( (short)223, ProtocolFamily.DiscoCustom ),
	Custom_224           ( (short)224, ProtocolFamily.DiscoCustom ),
	Custom_225           ( (short)225, ProtocolFamily.DiscoCustom ),
	Custom_226           ( (short)226, ProtocolFamily.DiscoCustom ),
	Custom_227           ( (short)227, ProtocolFamily.DiscoCustom ),
	Custom_228           ( (short)228, ProtocolFamily.DiscoCustom ),
	Custom_229           ( (short)229, ProtocolFamily.DiscoCustom ),
	// 230-239
	Custom_230           ( (short)230, ProtocolFamily.DiscoCustom ),
	Custom_231           ( (short)231, ProtocolFamily.DiscoCustom ),
	Custom_232           ( (short)232, ProtocolFamily.DiscoCustom ),
	Custom_233           ( (short)233, ProtocolFamily.DiscoCustom ),
	Custom_234           ( (short)234, ProtocolFamily.DiscoCustom ),
	Custom_235           ( (short)235, ProtocolFamily.DiscoCustom ),
	Custom_236           ( (short)236, ProtocolFamily.DiscoCustom ),
	Custom_237           ( (short)237, ProtocolFamily.DiscoCustom ),
	Custom_238           ( (short)238, ProtocolFamily.DiscoCustom ),
	Custom_239           ( (short)239, ProtocolFamily.DiscoCustom ),
	// 240-249
	Custom_240           ( (short)240, ProtocolFamily.DiscoCustom ),
	Custom_241           ( (short)241, ProtocolFamily.DiscoCustom ),
	Custom_242           ( (short)242, ProtocolFamily.DiscoCustom ),
	Custom_243           ( (short)243, ProtocolFamily.DiscoCustom ),
	Custom_244           ( (short)244, ProtocolFamily.DiscoCustom ),
	Custom_245           ( (short)245, ProtocolFamily.DiscoCustom ),
	Custom_246           ( (short)246, ProtocolFamily.DiscoCustom ),
	Custom_247           ( (short)247, ProtocolFamily.DiscoCustom ),
	Custom_248           ( (short)248, ProtocolFamily.DiscoCustom ),
	Custom_249           ( (short)249, ProtocolFamily.DiscoCustom ),
	// 250-259
	Custom_250           ( (short)250, ProtocolFamily.DiscoCustom ),
	Custom_251           ( (short)251, ProtocolFamily.DiscoCustom ),
	Custom_252           ( (short)252, ProtocolFamily.DiscoCustom ),
	Custom_253           ( (short)253, ProtocolFamily.DiscoCustom ),
	Custom_254           ( (short)254, ProtocolFamily.DiscoCustom ),
	Custom_255           ( (short)255, ProtocolFamily.DiscoCustom );
	
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final EnumLookup<PduType> DISVALUE_LOOKUP = new EnumLookup<>( PduType.class, 
	                                                                             PduType::value, 
	                                                                             PduType.Other );
	
	private static final EnumSet<PduType> SUPPORTED_TYPES = EnumSet.copyOf( Arrays.stream( PduType.values() )
	                                                                              .filter( type -> type.isSupported() )
	                                                                              .collect( Collectors.toList() ) );
	                                                              

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private final short value;
	private Class<? extends PDU> type;
	private ProtocolFamily protocolFamily;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private PduType( short value, ProtocolFamily family )
	{
		this.value = value;
		this.type = null;
		this.protocolFamily = family;
	}
	
	private PduType( short value, ProtocolFamily family, Class<? extends PDU> type )
	{
		this.value = value;
		this.type = type;
		this.protocolFamily = family;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public short value()
	{
		return this.value;
	}

	/**
	 * Returns the class that implements this PDU, or null if there is none.
	 */
	public Class<? extends PDU> getImplementationClass()
	{
		return this.type;
	}
	
	/**
	 * @return true if we have an associated Disco class/type for this PduType. false otherwise.
	 */
	public boolean isSupported()
	{
		return this.type != null;
	}

	public ProtocolFamily getProtocolFamily()
	{
		return this.protocolFamily;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static int getByteLength()
	{
		return DisSizes.UI8_SIZE;
	}

	public static PduType fromValue( short value )
	{
		return DISVALUE_LOOKUP.fromValue( value );
	}

	/**
	 * @return A list of all the types that we can instantiate. This is only the PDU types for which
	 *         we have a Class and Constructor cached.
	 */
	public static List<PduType> getSupportedPduTypes()
	{
		LinkedList<PduType> list = new LinkedList<>();
		for( PduType type : values() )
		{
			if( type.isSupported() )
				list.add( type );
		}
		
		return list;
	}
	
	public static EnumSet<PduType> getSupportedPdus()
	{
		return SUPPORTED_TYPES;
	}

	/**
	 * Returns a String representing the given PDU type. If the type is non-standard, the string
	 * "Custom(XX)" will be returned, where XX is the type id. If the type is standard, then the
	 * name of the PDU will be returned.
	 *  
	 * @param type The ID of the type we are looking for
	 * @return A string with the PDU type name
	 */
	public static String toString( short type )
	{
		if( type > 72 )
			return "Custom("+type+")";
		else
			return fromValue(type).name();
	}
	

}
