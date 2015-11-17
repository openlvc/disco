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

import org.openlvc.disco.pdu.DisSizes;

public enum Country
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	Other( 0 ),
	Afghanistan( 1 ),
	Albania( 2 ),
	Algeria( 3 ),
	AmericanSamoa( 4 ),
	Andorra( 5 ),
	Angola( 6 ),
	Anguilla( 7 ),
	Antarctica( 8 ),
	AntiguaAndBarbuda( 9 ),
	Argentina( 10 ),
	Aruba( 11 ),
	AshmoreAndCartierIslands( 12 ),
	Australia( 13 ),
	Austria( 14 ),
	Bahamas( 15 ),
	Bahrain( 16 ),
	BakerIsland( 17 ),
	Bangladesh( 18 ),
	Barbados( 19 ),
	BassasDaIndia( 20 ),
	Belgium( 21 ),
	Belize( 22 ),
	Benin( 23 ),
	Bermuda( 24 ),
	Bhutan( 25 ),
	Bolivia( 26 ),
	Botswana( 27 ),
	Bouvet_island( 28 ),
	Brazil( 29 ),
	BritishIndianOceanTerritory( 30 ),
	BritishVirginIslands( 31 ),
	Brunei( 32 ),
	Bulgaria( 33 ),
	Burkina( 34 ),
	Burma( 35 ),
	Burundi( 36 ),
	Cambodia( 37 ),
	Cameroon( 38 ),
	Canada( 39 ),
	CapeVerdeRepublicOf( 40 ),
	CaymanIslands( 41 ),
	CentralAfricanRepublic( 42 ),
	Chad( 43 ),
	Chile( 44 ),
	China( 45 ),
	ChristmasIsland( 46 ),
	Cocos( 47 ),
	Colombia( 48 ),
	Comoros( 49 ),
	Congo( 50 ),
	CookIslands( 51 ),
	CoralSeaIslands( 52 ),
	CostaRica( 53 ),
	Cuba( 54 ),
	Cyprus( 55 ),
	Czechoslovakia( 56 ),
	Denmark( 57 ),
	Djibouti( 58 ),
	Dominica( 59 ),
	DominicanRepublic( 60 ),
	Ecuador( 61 ),
	Egypt( 62 ),
	ElSalvador( 63 ),
	Equatorial_guinea( 64 ),
	Ethiopia( 65 ),
	EuropaIsland( 66 ),
	FalklandIslands( 67 ),
	FaroeIslands( 68 ),
	Fiji( 69 ),
	Finland( 70 ),
	France( 71 ),
	FrenchGuiana( 72 ),
	FrenchPolynesia( 73 ),
	FrenchSouthernAndAntarcticIslands( 74 ),
	Gabon( 75 ),
	Gambia( 76 ),
	GazaStrip( 77 ),
	Germany( 78 ),
	Ghana( 79 ),
	Gibraltar( 80 ),
	GloriosoIslands( 81 ),
	Greece( 82 ),
	Greenland( 83 ),
	Grenada( 84 ),
	Guadaloupe( 85 ),
	Guam( 86 ),
	Guatemala( 87 ),
	Guernsey( 88 ),
	Guinea( 89 ),
	GuineaBissau( 90 ),
	Guyana( 91 ),
	Haiti( 92 ),
	HeardIslandAndMcdonaldIslands( 93 ),
	Honduras( 94 ),
	HongKong( 95 ),
	HowlandIsland( 96 ),
	Hungary( 97 ),
	Iceland( 98 ),
	India( 99 ),
	Indonesia( 100 ),
	Iran( 101 ),
	Iraq( 102 ),
	Ireland( 104 ),
	Israel( 105 ),
	Italy( 106 ),
	CoteDivoire( 107 ),
	Jamaica( 108 ),
	JanMayen( 109 ),
	Japan( 110 ),
	JarvisIsland( 111 ),
	Jersey( 112 ),
	JohnstonAtoll( 113 ),
	Jordan( 114 ),
	JuanDeNovaIsland( 115 ),
	Kenya( 116 ),
	KingmanReef( 117 ),
	Kiribati( 118 ),
	KoreaDemocraticPeoplesRepublicOf( 119 ),
	KoreaRepublicOf( 120 ),
	Kuwait( 121 ),
	Laos( 122 ),
	Lebanon( 123 ),
	Lesotho( 124 ),
	Liberia( 125 ),
	Libya( 126 ),
	Liechtenstein( 127 ),
	Luxembourg( 128 ),
	Madagascar( 129 ),
	Macau( 130 ),
	Malawi( 131 ),
	Malaysia( 132 ),
	Maldives( 133 ),
	Mali( 134 ),
	Malta( 135 ),
	IsleOfMan( 136 ),
	MarshallIslands( 137 ),
	Martinique( 138 ),
	Mauritania( 139 ),
	Mauritius( 140 ),
	Mayotte( 141 ),
	Mexico( 142 ),
	Micronesia( 143 ),
	Monaco( 144 ),
	Mongolia( 145 ),
	Montserrat( 146 ),
	Morocco( 147 ),
	Mozambique( 148 ),
	Namibia( 149 ),
	Nauru( 150 ),
	NavassaIsland( 151 ),
	Nepal( 152 ),
	Netherlands( 153 ),
	NetherlandsAntilles( 154 ),
	NewCaledonia( 155 ),
	NewZealand( 156 ),
	Nicaragua( 157 ),
	Niger( 158 ),
	Nigeria( 159 ),
	Niue( 160 ),
	NorfolkIsland( 161 ),
	NorthernMarianaIslands( 162 ),
	Norway( 163 ),
	Oman( 164 ),
	Pakistan( 165 ),
	PalmyraAtoll( 166 ),
	Panama( 168 ),
	PapuaNewGuinea( 169 ),
	ParacelIslands( 170 ),
	Paraguay( 171 ),
	Peru( 172 ),
	Philippines( 173 ),
	PitcairnIslands( 174 ),
	Poland( 175 ),
	Portugal( 176 ),
	PuertoRico( 177 ),
	Qatar( 178 ),
	Reunion( 179 ),
	Romania( 180 ),
	Rwanda( 181 ),
	StKittsAndNevis( 182 ),
	StHelena( 183 ),
	StLucia( 184 ),
	StPierreAndMiquelon( 185 ),
	StVincentAndTheGrenadines( 186 ),
	SanMarino( 187 ),
	SaoTomeAndPrincipe( 188 ),
	SaudiArabia( 189 ),
	Senegal( 190 ),
	Seychelles( 191 ),
	Sierra_leone( 192 ),
	Singapore( 193 ),
	Solomon_islands( 194 ),
	Somalia( 195 ),
	SouthGeorgiaAndTheSouthSandwichIslands( 196 ),
	SouthAfrica( 197 ),
	Spain( 198 ),
	SpratlyIslands( 199 ),
	SriLanka( 200 ),
	Sudan( 201 ),
	Suriname( 202 ),
	Svalbard( 203 ),
	Swaziland( 204 ),
	Sweden( 205 ),
	Switzerland( 206 ),
	Syria( 207 ),
	Taiwan( 208 ),
	Tanzania( 209 ),
	Thailand( 210 ),
	Togo( 211 ),
	Tokelau( 212 ),
	Tonga( 213 ),
	TrinidadAndTobago( 214 ),
	Tromelin_island( 215 ),
	PacificIslandsTrustTerritoryOfThe( 216 ),
	Tunisia( 217 ),
	Turkey( 218 ),
	TurksAndCaicosIslands( 219 ),
	Tuvalu( 220 ),
	Uganda( 221 ),
	CommonwealthOfIndependentStates( 222 ),
	UnitedArabEmirates( 223 ),
	UnitedKingdom( 224 ),
	UnitedStates( 225 ),
	Uruguay( 226 ),
	Vanuatu( 227 ),
	VaticanCity( 228 ),
	Venezuela( 229 ),
	Vietnam( 230 ),
	VirginIslands( 231 ),
	WakeIsland( 232 ),
	WallisAndFutuna( 233 ),
	WesternSahara( 234 ),
	WestBank( 235 ),
	WesternSamoa( 236 ),
	Yemen( 237 ),
	Zaire( 241 ),
	Zambia( 242 ),
	Zimbabwe( 243 ),
	Armenia( 244 ),
	Azerbaijan( 245 ),
	Belarus( 246 ),
	BosniaAndHercegovina( 247 ),
	ClippertonIsland( 248 ),
	Croatia( 249 ),
	Estonia( 250 ),
	Georgia( 251 ),
	Kazakhstan( 252 ),
	Kyrgyzstan( 253 ),
	Latvia( 254 ),
	Lithuania( 255 ),
	Macedonia( 256 ),
	MidwayIslands( 257 ),
	Moldova( 258 ),
	Montenegro( 259 ),
	Russia( 260 ),
	SerbiaAndMontenegro( 261 ),
	Slovenia( 262 ),
	Tajikistan( 263 ),
	Turkmenistan( 264 ),
	Ukraine( 265 ),
	Uzbekistan( 266 );

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private int value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private Country( int value )
	{
		this.value = value;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public int value()
	{
		return this.value;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static int getByteLength()
	{
		return DisSizes.UI16_SIZE;
	}

	public static Country fromValue( int value )
	{
		if( value == UnitedStates.value ) return UnitedStates;
		else if( value == UnitedKingdom.value ) return UnitedKingdom;
		else if( value == Germany.value ) return Germany;
		else if( value == Australia.value ) return Australia;
		else if( value == Austria.value ) return Austria;
		else if( value == Sweden.value ) return Sweden;
		else if( value == Norway.value ) return Norway;
		else if( value == Denmark.value ) return Denmark;
		else if( value == NewZealand.value ) return NewZealand;
		else if( value == Greece.value ) return Greece;
		else if( value == Israel.value ) return Israel;
		else if( value == Japan.value ) return Japan;
		else if( value == China.value ) return China;
		
		for( Country country : values() )
		{
			if( country.value == value )
				return country;
		}
		
		throw new IllegalArgumentException( value+" not a valid Country Code" );
	}
}
