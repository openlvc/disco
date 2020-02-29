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
package org.openlvc.disco.connection.rpr.types.enumerated;

import java.util.HashMap;

import org.openlvc.disco.connection.rpr.types.basic.RPRunsignedInteger32BE;

import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderException;

public enum StationEnum32 implements ExtendedDataElement<StationEnum32>
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	Nothing_Empty( new RPRunsignedInteger32BE(0) ),
	Fuselage_Station1( new RPRunsignedInteger32BE(512) ),
	Fuselage_Station2( new RPRunsignedInteger32BE(513) ),
	Fuselage_Station3( new RPRunsignedInteger32BE(514) ),
	Fuselage_Station4( new RPRunsignedInteger32BE(515) ),
	Fuselage_Station5( new RPRunsignedInteger32BE(516) ),
	Fuselage_Station6( new RPRunsignedInteger32BE(517) ),
	Fuselage_Station7( new RPRunsignedInteger32BE(518) ),
	Fuselage_Station8( new RPRunsignedInteger32BE(519) ),
	Fuselage_Station9( new RPRunsignedInteger32BE(520) ),
	Fuselage_Station10( new RPRunsignedInteger32BE(521) ),
	Fuselage_Station11( new RPRunsignedInteger32BE(522) ),
	Fuselage_Station12( new RPRunsignedInteger32BE(523) ),
	Fuselage_Station13( new RPRunsignedInteger32BE(524) ),
	Fuselage_Station14( new RPRunsignedInteger32BE(525) ),
	Fuselage_Station15( new RPRunsignedInteger32BE(526) ),
	Fuselage_Station16( new RPRunsignedInteger32BE(527) ),
	Fuselage_Station17( new RPRunsignedInteger32BE(528) ),
	Fuselage_Station18( new RPRunsignedInteger32BE(529) ),
	Fuselage_Station19( new RPRunsignedInteger32BE(530) ),
	Fuselage_Station20( new RPRunsignedInteger32BE(531) ),
	Fuselage_Station21( new RPRunsignedInteger32BE(532) ),
	Fuselage_Station22( new RPRunsignedInteger32BE(533) ),
	Fuselage_Station23( new RPRunsignedInteger32BE(534) ),
	Fuselage_Station24( new RPRunsignedInteger32BE(535) ),
	Fuselage_Station25( new RPRunsignedInteger32BE(536) ),
	Fuselage_Station26( new RPRunsignedInteger32BE(537) ),
	Fuselage_Station27( new RPRunsignedInteger32BE(538) ),
	Fuselage_Station28( new RPRunsignedInteger32BE(539) ),
	Fuselage_Station29( new RPRunsignedInteger32BE(540) ),
	Fuselage_Station30( new RPRunsignedInteger32BE(541) ),
	Fuselage_Station31( new RPRunsignedInteger32BE(542) ),
	Fuselage_Station32( new RPRunsignedInteger32BE(543) ),
	Fuselage_Station33( new RPRunsignedInteger32BE(544) ),
	Fuselage_Station34( new RPRunsignedInteger32BE(545) ),
	Fuselage_Station35( new RPRunsignedInteger32BE(546) ),
	Fuselage_Station36( new RPRunsignedInteger32BE(547) ),
	Fuselage_Station37( new RPRunsignedInteger32BE(548) ),
	Fuselage_Station38( new RPRunsignedInteger32BE(549) ),
	Fuselage_Station39( new RPRunsignedInteger32BE(550) ),
	Fuselage_Station40( new RPRunsignedInteger32BE(551) ),
	Fuselage_Station41( new RPRunsignedInteger32BE(552) ),
	Fuselage_Station42( new RPRunsignedInteger32BE(553) ),
	Fuselage_Station43( new RPRunsignedInteger32BE(554) ),
	Fuselage_Station44( new RPRunsignedInteger32BE(555) ),
	Fuselage_Station45( new RPRunsignedInteger32BE(556) ),
	Fuselage_Station46( new RPRunsignedInteger32BE(557) ),
	Fuselage_Station47( new RPRunsignedInteger32BE(558) ),
	Fuselage_Station48( new RPRunsignedInteger32BE(559) ),
	Fuselage_Station49( new RPRunsignedInteger32BE(560) ),
	Fuselage_Station50( new RPRunsignedInteger32BE(561) ),
	Fuselage_Station51( new RPRunsignedInteger32BE(562) ),
	Fuselage_Station52( new RPRunsignedInteger32BE(563) ),
	Fuselage_Station53( new RPRunsignedInteger32BE(564) ),
	Fuselage_Station54( new RPRunsignedInteger32BE(565) ),
	Fuselage_Station55( new RPRunsignedInteger32BE(566) ),
	Fuselage_Station56( new RPRunsignedInteger32BE(567) ),
	Fuselage_Station57( new RPRunsignedInteger32BE(568) ),
	Fuselage_Station58( new RPRunsignedInteger32BE(569) ),
	Fuselage_Station59( new RPRunsignedInteger32BE(570) ),
	Fuselage_Station60( new RPRunsignedInteger32BE(571) ),
	Fuselage_Station61( new RPRunsignedInteger32BE(572) ),
	Fuselage_Station62( new RPRunsignedInteger32BE(573) ),
	Fuselage_Station63( new RPRunsignedInteger32BE(574) ),
	Fuselage_Station64( new RPRunsignedInteger32BE(575) ),
	Fuselage_Station65( new RPRunsignedInteger32BE(576) ),
	Fuselage_Station66( new RPRunsignedInteger32BE(577) ),
	Fuselage_Station67( new RPRunsignedInteger32BE(578) ),
	Fuselage_Station68( new RPRunsignedInteger32BE(579) ),
	Fuselage_Station69( new RPRunsignedInteger32BE(580) ),
	Fuselage_Station70( new RPRunsignedInteger32BE(581) ),
	Fuselage_Station71( new RPRunsignedInteger32BE(582) ),
	Fuselage_Station72( new RPRunsignedInteger32BE(583) ),
	Fuselage_Station73( new RPRunsignedInteger32BE(584) ),
	Fuselage_Station74( new RPRunsignedInteger32BE(585) ),
	Fuselage_Station75( new RPRunsignedInteger32BE(586) ),
	Fuselage_Station76( new RPRunsignedInteger32BE(587) ),
	Fuselage_Station77( new RPRunsignedInteger32BE(588) ),
	Fuselage_Station78( new RPRunsignedInteger32BE(589) ),
	Fuselage_Station79( new RPRunsignedInteger32BE(590) ),
	Fuselage_Station80( new RPRunsignedInteger32BE(591) ),
	Fuselage_Station81( new RPRunsignedInteger32BE(592) ),
	Fuselage_Station82( new RPRunsignedInteger32BE(593) ),
	Fuselage_Station83( new RPRunsignedInteger32BE(594) ),
	Fuselage_Station84( new RPRunsignedInteger32BE(595) ),
	Fuselage_Station85( new RPRunsignedInteger32BE(596) ),
	Fuselage_Station86( new RPRunsignedInteger32BE(597) ),
	Fuselage_Station87( new RPRunsignedInteger32BE(598) ),
	Fuselage_Station88( new RPRunsignedInteger32BE(599) ),
	Fuselage_Station89( new RPRunsignedInteger32BE(600) ),
	Fuselage_Station90( new RPRunsignedInteger32BE(601) ),
	Fuselage_Station91( new RPRunsignedInteger32BE(602) ),
	Fuselage_Station92( new RPRunsignedInteger32BE(603) ),
	Fuselage_Station93( new RPRunsignedInteger32BE(604) ),
	Fuselage_Station94( new RPRunsignedInteger32BE(605) ),
	Fuselage_Station95( new RPRunsignedInteger32BE(606) ),
	Fuselage_Station96( new RPRunsignedInteger32BE(607) ),
	Fuselage_Station97( new RPRunsignedInteger32BE(608) ),
	Fuselage_Station98( new RPRunsignedInteger32BE(609) ),
	Fuselage_Station99( new RPRunsignedInteger32BE(610) ),
	Fuselage_Station100( new RPRunsignedInteger32BE(611) ),
	Fuselage_Station101( new RPRunsignedInteger32BE(612) ),
	Fuselage_Station102( new RPRunsignedInteger32BE(613) ),
	Fuselage_Station103( new RPRunsignedInteger32BE(614) ),
	Fuselage_Station104( new RPRunsignedInteger32BE(615) ),
	Fuselage_Station105( new RPRunsignedInteger32BE(616) ),
	Fuselage_Station106( new RPRunsignedInteger32BE(617) ),
	Fuselage_Station107( new RPRunsignedInteger32BE(618) ),
	Fuselage_Station108( new RPRunsignedInteger32BE(619) ),
	Fuselage_Station109( new RPRunsignedInteger32BE(620) ),
	Fuselage_Station110( new RPRunsignedInteger32BE(621) ),
	Fuselage_Station111( new RPRunsignedInteger32BE(622) ),
	Fuselage_Station112( new RPRunsignedInteger32BE(623) ),
	Fuselage_Station113( new RPRunsignedInteger32BE(624) ),
	Fuselage_Station114( new RPRunsignedInteger32BE(625) ),
	Fuselage_Station115( new RPRunsignedInteger32BE(626) ),
	Fuselage_Station116( new RPRunsignedInteger32BE(627) ),
	Fuselage_Station117( new RPRunsignedInteger32BE(628) ),
	Fuselage_Station118( new RPRunsignedInteger32BE(629) ),
	Fuselage_Station119( new RPRunsignedInteger32BE(630) ),
	Fuselage_Station120( new RPRunsignedInteger32BE(631) ),
	Fuselage_Station121( new RPRunsignedInteger32BE(632) ),
	Fuselage_Station122( new RPRunsignedInteger32BE(633) ),
	Fuselage_Station123( new RPRunsignedInteger32BE(634) ),
	Fuselage_Station124( new RPRunsignedInteger32BE(635) ),
	Fuselage_Station125( new RPRunsignedInteger32BE(636) ),
	Fuselage_Station126( new RPRunsignedInteger32BE(637) ),
	Fuselage_Station127( new RPRunsignedInteger32BE(638) ),
	Fuselage_Station128( new RPRunsignedInteger32BE(639) ),
	LeftWingStation1( new RPRunsignedInteger32BE(640) ),
	LeftWingStation2( new RPRunsignedInteger32BE(641) ),
	LeftWingStation3( new RPRunsignedInteger32BE(642) ),
	LeftWingStation4( new RPRunsignedInteger32BE(643) ),
	LeftWingStation5( new RPRunsignedInteger32BE(644) ),
	LeftWingStation6( new RPRunsignedInteger32BE(645) ),
	LeftWingStation7( new RPRunsignedInteger32BE(646) ),
	LeftWingStation8( new RPRunsignedInteger32BE(647) ),
	LeftWingStation9( new RPRunsignedInteger32BE(648) ),
	LeftWingStation10( new RPRunsignedInteger32BE(649) ),
	LeftWingStation11( new RPRunsignedInteger32BE(650) ),
	LeftWingStation12( new RPRunsignedInteger32BE(651) ),
	LeftWingStation13( new RPRunsignedInteger32BE(652) ),
	LeftWingStation14( new RPRunsignedInteger32BE(653) ),
	LeftWingStation15( new RPRunsignedInteger32BE(654) ),
	LeftWingStation16( new RPRunsignedInteger32BE(655) ),
	LeftWingStation17( new RPRunsignedInteger32BE(656) ),
	LeftWingStation18( new RPRunsignedInteger32BE(657) ),
	LeftWingStation19( new RPRunsignedInteger32BE(658) ),
	LeftWingStation20( new RPRunsignedInteger32BE(659) ),
	LeftWingStation21( new RPRunsignedInteger32BE(660) ),
	LeftWingStation22( new RPRunsignedInteger32BE(661) ),
	LeftWingStation23( new RPRunsignedInteger32BE(662) ),
	LeftWingStation24( new RPRunsignedInteger32BE(663) ),
	LeftWingStation25( new RPRunsignedInteger32BE(664) ),
	LeftWingStation26( new RPRunsignedInteger32BE(665) ),
	LeftWingStation27( new RPRunsignedInteger32BE(666) ),
	LeftWingStation28( new RPRunsignedInteger32BE(667) ),
	LeftWingStation29( new RPRunsignedInteger32BE(668) ),
	LeftWingStation30( new RPRunsignedInteger32BE(669) ),
	LeftWingStation31( new RPRunsignedInteger32BE(670) ),
	LeftWingStation32( new RPRunsignedInteger32BE(671) ),
	LeftWingStation33( new RPRunsignedInteger32BE(672) ),
	LeftWingStation34( new RPRunsignedInteger32BE(673) ),
	LeftWingStation35( new RPRunsignedInteger32BE(674) ),
	LeftWingStation36( new RPRunsignedInteger32BE(675) ),
	LeftWingStation37( new RPRunsignedInteger32BE(676) ),
	LeftWingStation38( new RPRunsignedInteger32BE(677) ),
	LeftWingStation39( new RPRunsignedInteger32BE(678) ),
	LeftWingStation40( new RPRunsignedInteger32BE(679) ),
	LeftWingStation41( new RPRunsignedInteger32BE(680) ),
	LeftWingStation42( new RPRunsignedInteger32BE(681) ),
	LeftWingStation43( new RPRunsignedInteger32BE(682) ),
	LeftWingStation44( new RPRunsignedInteger32BE(683) ),
	LeftWingStation45( new RPRunsignedInteger32BE(684) ),
	LeftWingStation46( new RPRunsignedInteger32BE(685) ),
	LeftWingStation47( new RPRunsignedInteger32BE(686) ),
	LeftWingStation48( new RPRunsignedInteger32BE(687) ),
	LeftWingStation49( new RPRunsignedInteger32BE(688) ),
	LeftWingStation50( new RPRunsignedInteger32BE(689) ),
	LeftWingStation51( new RPRunsignedInteger32BE(690) ),
	LeftWingStation52( new RPRunsignedInteger32BE(691) ),
	LeftWingStation53( new RPRunsignedInteger32BE(692) ),
	LeftWingStation54( new RPRunsignedInteger32BE(693) ),
	LeftWingStation55( new RPRunsignedInteger32BE(694) ),
	LeftWingStation56( new RPRunsignedInteger32BE(695) ),
	LeftWingStation57( new RPRunsignedInteger32BE(696) ),
	LeftWingStation58( new RPRunsignedInteger32BE(697) ),
	LeftWingStation59( new RPRunsignedInteger32BE(698) ),
	LeftWingStation60( new RPRunsignedInteger32BE(699) ),
	LeftWingStation61( new RPRunsignedInteger32BE(700) ),
	LeftWingStation62( new RPRunsignedInteger32BE(701) ),
	LeftWingStation63( new RPRunsignedInteger32BE(702) ),
	LeftWingStation64( new RPRunsignedInteger32BE(703) ),
	LeftWingStation65( new RPRunsignedInteger32BE(704) ),
	LeftWingStation66( new RPRunsignedInteger32BE(705) ),
	LeftWingStation67( new RPRunsignedInteger32BE(706) ),
	LeftWingStation68( new RPRunsignedInteger32BE(707) ),
	LeftWingStation69( new RPRunsignedInteger32BE(708) ),
	LeftWingStation70( new RPRunsignedInteger32BE(709) ),
	LeftWingStation71( new RPRunsignedInteger32BE(710) ),
	LeftWingStation72( new RPRunsignedInteger32BE(711) ),
	LeftWingStation73( new RPRunsignedInteger32BE(712) ),
	LeftWingStation74( new RPRunsignedInteger32BE(713) ),
	LeftWingStation75( new RPRunsignedInteger32BE(714) ),
	LeftWingStation76( new RPRunsignedInteger32BE(715) ),
	LeftWingStation77( new RPRunsignedInteger32BE(716) ),
	LeftWingStation78( new RPRunsignedInteger32BE(717) ),
	LeftWingStation79( new RPRunsignedInteger32BE(718) ),
	LeftWingStation80( new RPRunsignedInteger32BE(719) ),
	LeftWingStation81( new RPRunsignedInteger32BE(720) ),
	LeftWingStation82( new RPRunsignedInteger32BE(721) ),
	LeftWingStation83( new RPRunsignedInteger32BE(722) ),
	LeftWingStation84( new RPRunsignedInteger32BE(723) ),
	LeftWingStation85( new RPRunsignedInteger32BE(724) ),
	LeftWingStation86( new RPRunsignedInteger32BE(725) ),
	LeftWingStation87( new RPRunsignedInteger32BE(726) ),
	LeftWingStation88( new RPRunsignedInteger32BE(727) ),
	LeftWingStation89( new RPRunsignedInteger32BE(728) ),
	LeftWingStation90( new RPRunsignedInteger32BE(729) ),
	LeftWingStation91( new RPRunsignedInteger32BE(730) ),
	LeftWingStation92( new RPRunsignedInteger32BE(731) ),
	LeftWingStation93( new RPRunsignedInteger32BE(732) ),
	LeftWingStation94( new RPRunsignedInteger32BE(733) ),
	LeftWingStation95( new RPRunsignedInteger32BE(734) ),
	LeftWingStation96( new RPRunsignedInteger32BE(735) ),
	LeftWingStation97( new RPRunsignedInteger32BE(736) ),
	LeftWingStation98( new RPRunsignedInteger32BE(737) ),
	LeftWingStation99( new RPRunsignedInteger32BE(738) ),
	LeftWingStation100( new RPRunsignedInteger32BE(739) ),
	LeftWingStation101( new RPRunsignedInteger32BE(740) ),
	LeftWingStation102( new RPRunsignedInteger32BE(741) ),
	LeftWingStation103( new RPRunsignedInteger32BE(742) ),
	LeftWingStation104( new RPRunsignedInteger32BE(743) ),
	LeftWingStation105( new RPRunsignedInteger32BE(744) ),
	LeftWingStation106( new RPRunsignedInteger32BE(745) ),
	LeftWingStation107( new RPRunsignedInteger32BE(746) ),
	LeftWingStation108( new RPRunsignedInteger32BE(747) ),
	LeftWingStation109( new RPRunsignedInteger32BE(748) ),
	LeftWingStation110( new RPRunsignedInteger32BE(749) ),
	LeftWingStation111( new RPRunsignedInteger32BE(750) ),
	LeftWingStation112( new RPRunsignedInteger32BE(751) ),
	LeftWingStation113( new RPRunsignedInteger32BE(752) ),
	LeftWingStation114( new RPRunsignedInteger32BE(753) ),
	LeftWingStation115( new RPRunsignedInteger32BE(754) ),
	LeftWingStation116( new RPRunsignedInteger32BE(755) ),
	LeftWingStation117( new RPRunsignedInteger32BE(756) ),
	LeftWingStation118( new RPRunsignedInteger32BE(757) ),
	LeftWingStation119( new RPRunsignedInteger32BE(758) ),
	LeftWingStation120( new RPRunsignedInteger32BE(759) ),
	LeftWingStation121( new RPRunsignedInteger32BE(760) ),
	LeftWingStation122( new RPRunsignedInteger32BE(761) ),
	LeftWingStation123( new RPRunsignedInteger32BE(762) ),
	LeftWingStation124( new RPRunsignedInteger32BE(763) ),
	LeftWingStation125( new RPRunsignedInteger32BE(764) ),
	LeftWingStation126( new RPRunsignedInteger32BE(765) ),
	LeftWingStation127( new RPRunsignedInteger32BE(766) ),
	LeftWingStation128( new RPRunsignedInteger32BE(767) ),
	RightWingStation1( new RPRunsignedInteger32BE(768) ),
	RightWingStation2( new RPRunsignedInteger32BE(769) ),
	RightWingStation3( new RPRunsignedInteger32BE(770) ),
	RightWingStation4( new RPRunsignedInteger32BE(771) ),
	RightWingStation5( new RPRunsignedInteger32BE(772) ),
	RightWingStation6( new RPRunsignedInteger32BE(773) ),
	RightWingStation7( new RPRunsignedInteger32BE(774) ),
	RightWingStation8( new RPRunsignedInteger32BE(775) ),
	RightWingStation9( new RPRunsignedInteger32BE(776) ),
	RightWingStation10( new RPRunsignedInteger32BE(777) ),
	RightWingStation11( new RPRunsignedInteger32BE(778) ),
	RightWingStation12( new RPRunsignedInteger32BE(779) ),
	RightWingStation13( new RPRunsignedInteger32BE(780) ),
	RightWingStation14( new RPRunsignedInteger32BE(781) ),
	RightWingStation15( new RPRunsignedInteger32BE(782) ),
	RightWingStation16( new RPRunsignedInteger32BE(783) ),
	RightWingStation17( new RPRunsignedInteger32BE(784) ),
	RightWingStation18( new RPRunsignedInteger32BE(785) ),
	RightWingStation19( new RPRunsignedInteger32BE(786) ),
	RightWingStation20( new RPRunsignedInteger32BE(787) ),
	RightWingStation21( new RPRunsignedInteger32BE(788) ),
	RightWingStation22( new RPRunsignedInteger32BE(789) ),
	RightWingStation23( new RPRunsignedInteger32BE(790) ),
	RightWingStation24( new RPRunsignedInteger32BE(791) ),
	RightWingStation25( new RPRunsignedInteger32BE(792) ),
	RightWingStation26( new RPRunsignedInteger32BE(793) ),
	RightWingStation27( new RPRunsignedInteger32BE(794) ),
	RightWingStation28( new RPRunsignedInteger32BE(795) ),
	RightWingStation29( new RPRunsignedInteger32BE(796) ),
	RightWingStation30( new RPRunsignedInteger32BE(797) ),
	RightWingStation31( new RPRunsignedInteger32BE(798) ),
	RightWingStation32( new RPRunsignedInteger32BE(799) ),
	RightWingStation33( new RPRunsignedInteger32BE(800) ),
	RightWingStation34( new RPRunsignedInteger32BE(801) ),
	RightWingStation35( new RPRunsignedInteger32BE(802) ),
	RightWingStation36( new RPRunsignedInteger32BE(803) ),
	RightWingStation37( new RPRunsignedInteger32BE(804) ),
	RightWingStation38( new RPRunsignedInteger32BE(805) ),
	RightWingStation39( new RPRunsignedInteger32BE(806) ),
	RightWingStation40( new RPRunsignedInteger32BE(807) ),
	RightWingStation41( new RPRunsignedInteger32BE(808) ),
	RightWingStation42( new RPRunsignedInteger32BE(809) ),
	RightWingStation43( new RPRunsignedInteger32BE(810) ),
	RightWingStation44( new RPRunsignedInteger32BE(811) ),
	RightWingStation45( new RPRunsignedInteger32BE(812) ),
	RightWingStation46( new RPRunsignedInteger32BE(813) ),
	RightWingStation47( new RPRunsignedInteger32BE(814) ),
	RightWingStation48( new RPRunsignedInteger32BE(815) ),
	RightWingStation49( new RPRunsignedInteger32BE(816) ),
	RightWingStation50( new RPRunsignedInteger32BE(817) ),
	RightWingStation51( new RPRunsignedInteger32BE(818) ),
	RightWingStation52( new RPRunsignedInteger32BE(819) ),
	RightWingStation53( new RPRunsignedInteger32BE(820) ),
	RightWingStation54( new RPRunsignedInteger32BE(821) ),
	RightWingStation55( new RPRunsignedInteger32BE(822) ),
	RightWingStation56( new RPRunsignedInteger32BE(823) ),
	RightWingStation57( new RPRunsignedInteger32BE(824) ),
	RightWingStation58( new RPRunsignedInteger32BE(825) ),
	RightWingStation59( new RPRunsignedInteger32BE(826) ),
	RightWingStation60( new RPRunsignedInteger32BE(827) ),
	RightWingStation61( new RPRunsignedInteger32BE(828) ),
	RightWingStation62( new RPRunsignedInteger32BE(829) ),
	RightWingStation63( new RPRunsignedInteger32BE(830) ),
	RightWingStation64( new RPRunsignedInteger32BE(831) ),
	RightWingStation65( new RPRunsignedInteger32BE(832) ),
	RightWingStation66( new RPRunsignedInteger32BE(833) ),
	RightWingStation67( new RPRunsignedInteger32BE(834) ),
	RightWingStation68( new RPRunsignedInteger32BE(835) ),
	RightWingStation69( new RPRunsignedInteger32BE(836) ),
	RightWingStation70( new RPRunsignedInteger32BE(837) ),
	RightWingStation71( new RPRunsignedInteger32BE(838) ),
	RightWingStation72( new RPRunsignedInteger32BE(839) ),
	RightWingStation73( new RPRunsignedInteger32BE(840) ),
	RightWingStation74( new RPRunsignedInteger32BE(841) ),
	RightWingStation75( new RPRunsignedInteger32BE(842) ),
	RightWingStation76( new RPRunsignedInteger32BE(843) ),
	RightWingStation77( new RPRunsignedInteger32BE(844) ),
	RightWingStation78( new RPRunsignedInteger32BE(845) ),
	RightWingStation79( new RPRunsignedInteger32BE(846) ),
	RightWingStation80( new RPRunsignedInteger32BE(847) ),
	RightWingStation81( new RPRunsignedInteger32BE(848) ),
	RightWingStation82( new RPRunsignedInteger32BE(849) ),
	RightWingStation83( new RPRunsignedInteger32BE(850) ),
	RightWingStation84( new RPRunsignedInteger32BE(851) ),
	RightWingStation85( new RPRunsignedInteger32BE(852) ),
	RightWingStation86( new RPRunsignedInteger32BE(853) ),
	RightWingStation87( new RPRunsignedInteger32BE(854) ),
	RightWingStation88( new RPRunsignedInteger32BE(855) ),
	RightWingStation89( new RPRunsignedInteger32BE(856) ),
	RightWingStation90( new RPRunsignedInteger32BE(857) ),
	RightWingStation91( new RPRunsignedInteger32BE(858) ),
	RightWingStation92( new RPRunsignedInteger32BE(859) ),
	RightWingStation93( new RPRunsignedInteger32BE(860) ),
	RightWingStation94( new RPRunsignedInteger32BE(861) ),
	RightWingStation95( new RPRunsignedInteger32BE(862) ),
	RightWingStation96( new RPRunsignedInteger32BE(863) ),
	RightWingStation97( new RPRunsignedInteger32BE(864) ),
	RightWingStation98( new RPRunsignedInteger32BE(865) ),
	RightWingStation99( new RPRunsignedInteger32BE(866) ),
	RightWingStation100( new RPRunsignedInteger32BE(867) ),
	RightWingStation101( new RPRunsignedInteger32BE(868) ),
	RightWingStation102( new RPRunsignedInteger32BE(869) ),
	RightWingStation103( new RPRunsignedInteger32BE(870) ),
	RightWingStation104( new RPRunsignedInteger32BE(871) ),
	RightWingStation105( new RPRunsignedInteger32BE(872) ),
	RightWingStation106( new RPRunsignedInteger32BE(873) ),
	RightWingStation107( new RPRunsignedInteger32BE(874) ),
	RightWingStation108( new RPRunsignedInteger32BE(875) ),
	RightWingStation109( new RPRunsignedInteger32BE(876) ),
	RightWingStation110( new RPRunsignedInteger32BE(877) ),
	RightWingStation111( new RPRunsignedInteger32BE(878) ),
	RightWingStation112( new RPRunsignedInteger32BE(879) ),
	RightWingStation113( new RPRunsignedInteger32BE(880) ),
	RightWingStation114( new RPRunsignedInteger32BE(881) ),
	RightWingStation115( new RPRunsignedInteger32BE(882) ),
	RightWingStation116( new RPRunsignedInteger32BE(883) ),
	RightWingStation117( new RPRunsignedInteger32BE(884) ),
	RightWingStation118( new RPRunsignedInteger32BE(885) ),
	RightWingStation119( new RPRunsignedInteger32BE(886) ),
	RightWingStation120( new RPRunsignedInteger32BE(887) ),
	RightWingStation121( new RPRunsignedInteger32BE(888) ),
	RightWingStation122( new RPRunsignedInteger32BE(889) ),
	RightWingStation123( new RPRunsignedInteger32BE(890) ),
	RightWingStation124( new RPRunsignedInteger32BE(891) ),
	RightWingStation125( new RPRunsignedInteger32BE(892) ),
	RightWingStation126( new RPRunsignedInteger32BE(893) ),
	RightWingStation127( new RPRunsignedInteger32BE(894) ),
	RightWingStation128( new RPRunsignedInteger32BE(895) ),
	M16A42_rifle( new RPRunsignedInteger32BE(896) ),
	M249_SAW( new RPRunsignedInteger32BE(897) ),
	M60_Machine_gun( new RPRunsignedInteger32BE(898) ),
	M203_Grenade_Launcher( new RPRunsignedInteger32BE(899) ),
	M136_AT4( new RPRunsignedInteger32BE(900) ),
	M47_Dragon( new RPRunsignedInteger32BE(901) ),
	AAWS_M_Javelin( new RPRunsignedInteger32BE(902) ),
	M18A1_Claymore_Mine( new RPRunsignedInteger32BE(903) ),
	MK19_Grenade_Launcher( new RPRunsignedInteger32BE(904) ),
	M2_Machine_Gun( new RPRunsignedInteger32BE(905) ),
	Other_attached_parts( new RPRunsignedInteger32BE(906) );

	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static HashMap<Long,StationEnum32> MAP = new HashMap<>();

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private RPRunsignedInteger32BE value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private StationEnum32( RPRunsignedInteger32BE value )
	{
		this.value = value;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	public long getValue()
	{
		return this.value.getValue();
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Data Element Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int getOctetBoundary()
	{
		return value.getOctetBoundary();
	}

	@Override
	public void encode( ByteWrapper byteWrapper ) throws EncoderException
	{
		value.encode( byteWrapper );
	}


	@Override
	public int getEncodedLength()
	{
		return value.getEncodedLength();
	}


	@Override
	public byte[] toByteArray() throws EncoderException
	{
		return value.toByteArray();
	}


	@Override
	public StationEnum32 valueOf( ByteWrapper value ) throws DecoderException
	{
		RPRunsignedInteger32BE temp = new RPRunsignedInteger32BE();
		temp.decode( value );
		return valueOf( temp.getValue() );
	}

	@Override
	public StationEnum32 valueOf( byte[] value ) throws DecoderException
	{
		RPRunsignedInteger32BE temp = new RPRunsignedInteger32BE();
		temp.decode( value );
		return valueOf( temp.getValue() );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static StationEnum32 valueOf( long value )
	{
		// Because there are so many values we put them in a map. Looking it up will be
		// faster than iterating through every single one each time
		if( MAP.isEmpty() )
		{
			for( StationEnum32 temp : StationEnum32.values() )
				MAP.put( temp.getValue(), temp );
		}
		
		StationEnum32 temp = MAP.get( value );
		if( temp == null )
			throw new IllegalArgumentException( "Unknown enumerator value: "+value+" (StationEnum32)" );
		else
			return temp;
	}
}
