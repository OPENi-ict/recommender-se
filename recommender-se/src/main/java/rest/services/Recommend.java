package rest.services;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import models.ContextPermissions;
import tempservices.FoursquareSearch;
import utilities.applications.ApplicationRecommender;
import utilities.places.Recommender;
import utilities.products.ProductRecommender;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import com.wordnik.swagger.annotations.ApiParam;


@Path("/recommender")
@Produces("application/json")
@Api(value = "/recommender", description = "recommender service enabler operations")
public class Recommend {

	@GET
	@Path("/places")
	@Produces("application/json")
	@ApiOperation(value = "Get place recommendations",httpMethod="GET", notes = "Get recommended places for OPENi user.")
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "Invalid path or wrong parameters specified"),
			@ApiResponse(code = 503, message = "Service unavailable") 
	})
	public Response getPlaces(
			@ApiParam(value="longitude",required=true, name="long") @NotNull @QueryParam("long") double longitude,
			@ApiParam(value="latitude",required=true, name="lat")@NotNull @QueryParam("lat") double latitude,
			@ApiParam(value="radius in meters-defaults to city-wide area",required=false, name="rad")@DefaultValue("3") @QueryParam("rad") float radiance,
			@ApiParam(value="criteria used to order recommendations",required=false, name="orderby",allowableValues = "score,name,distance")@DefaultValue("score") @QueryParam("orderby") String orderby,
			@ApiParam(value="user context to be taken into consideration",required=true, name="context",allowableValues = "all,gender,age,education,interests,country,ethnicity,family_status",allowMultiple=true)@DefaultValue("all") @QueryParam("context") String contextTypes,
			@ApiParam(value="context id",required=true, name="id")@DefaultValue("1") @QueryParam("id") String auth, 
			@ApiParam(value="unix timestamp",required=false, name="timestamp") @QueryParam("timestamp") String tms) {

		boolean useInterests = false;
		boolean useCurrentTime = true;
		boolean useAge = false;
		boolean useGender = false;
		boolean useEducation = false;
		boolean useCountry = false;
		boolean useEthnicity = false;


		try{

			if (tms==null)
				useCurrentTime = false;
			else
				try{
					Long.parseLong(tms);
				}
			catch(NumberFormatException e){
				return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"Parameter timestamp must be a valid unix timestamp\"}").build();
			}

			if (contextTypes.contains("all")) {
				useInterests = true;
				useAge = true;
				useEducation = true;
				useGender = true;
				useCountry = true;
				useEthnicity = true;
			}
			if (contextTypes.contains("gender")) {
				useGender = true;
			}
			if (contextTypes.contains("age")) {
				useAge = true;
			}
			if (contextTypes.contains("education")) {
				useEducation = true;
			}

			if (contextTypes.contains("interests")) {
				useInterests = true;
			}


			ContextPermissions permissions = new ContextPermissions(useInterests, useAge, useEducation, useGender, useCountry, useEthnicity, false, false, false, useCurrentTime);
			String categoryName = Recommender.getCategoryFromShortestPaths(auth,permissions,tms);
			FoursquareSearch fs = new FoursquareSearch();
			String fsq_response = fs.getFsquareNearbyVenues(latitude, longitude,
					categoryName);
			return Response.status(200).entity(fsq_response).build();
		}
		catch(Exception e){
			e.printStackTrace();
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("{\"error\":\"Service temporarily unavailable.\"}").build();
		}
	}

	@GET
	@Produces("application/json")
	@Path("/places/categories")
	@ApiOperation(value = "Get place categories",httpMethod="GET", notes = "You can find here the available place categories. Currently supported categories come from Foursquare.")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "No place categories found.") 
	})
	public Response getPlaceCategories() {

		String output;


		try {
			output = "{\"categories\":[{\"id\":\"frsq_302\",\"name\":\"Accessories Store\"},{\"id\":\"frsq_83\",\"name\":\"Afghan Restaurant\"},{\"id\":\"frsq_84\",\"name\":\"African Restaurant\"},{\"id\":\"frsq_376\",\"name\":\"Airport\"},{\"id\":\"frsq_377\",\"name\":\"Airport Food Court\"},{\"id\":\"frsq_378\",\"name\":\"Airport Gate\"},{\"id\":\"frsq_379\",\"name\":\"Airport Lounge\"},{\"id\":\"frsq_380\",\"name\":\"Airport Terminal\"},{\"id\":\"frsq_381\",\"name\":\"Airport Tram\"},{\"id\":\"frsq_85\",\"name\":\"American Restaurant\"},{\"id\":\"frsq_234\",\"name\":\"Animal Shelter\"},{\"id\":\"frsq_289\",\"name\":\"Antique Shop\"},{\"id\":\"frsq_224\",\"name\":\"Apres Ski bar\"},{\"id\":\"frsq_2\",\"name\":\"Aquarium\"},{\"id\":\"frsq_3\",\"name\":\"Arcade\"},{\"id\":\"frsq_86\",\"name\":\"Arepa Restaurant\"},{\"id\":\"frsq_87\",\"name\":\"Argentinian Restaurant\"},{\"id\":\"frsq_4\",\"name\":\"Art Gallery\"},{\"id\":\"frsq_15\",\"name\":\"Art Museum\"},{\"id\":\"frsq_290\",\"name\":\"Arts & Crafts Store\"},{\"id\":\"frsq_1\",\"name\":\"Arts & Entertainment\"},{\"id\":\"frsq_88\",\"name\":\"Asian Restaurant\"},{\"id\":\"frsq_284\",\"name\":\"Assisted Living\"},{\"id\":\"frsq_187\",\"name\":\"Athletic & Sport\"},{\"id\":\"frsq_235\",\"name\":\"Auditorium\"},{\"id\":\"frsq_89\",\"name\":\"Australian Restaurant\"},{\"id\":\"frsq_291\",\"name\":\"Automotive Shop\"},{\"id\":\"frsq_90\",\"name\":\"BBQ Joint\"},{\"id\":\"frsq_91\",\"name\":\"Bagel Shop\"},{\"id\":\"frsq_92\",\"name\":\"Bakery\"},{\"id\":\"frsq_292\",\"name\":\"Bank\"},{\"id\":\"frsq_168\",\"name\":\"Bar\"},{\"id\":\"frsq_188\",\"name\":\"Baseball Field\"},{\"id\":\"frsq_32\",\"name\":\"Baseball Stadium\"},{\"id\":\"frsq_189\",\"name\":\"Basketball Court\"},{\"id\":\"frsq_33\",\"name\":\"Basketball Stadium\"},{\"id\":\"frsq_198\",\"name\":\"Beach\"},{\"id\":\"frsq_392\",\"name\":\"Bed & Breakfast\"},{\"id\":\"frsq_169\",\"name\":\"Beer Garden\"},{\"id\":\"frsq_383\",\"name\":\"Bike Rental / Bike Share\"},{\"id\":\"frsq_293\",\"name\":\"Bike Shop\"},{\"id\":\"frsq_294\",\"name\":\"Board Shop\"},{\"id\":\"frsq_393\",\"name\":\"Boarding House\"},{\"id\":\"frsq_388\",\"name\":\"Boat or Ferry\"},{\"id\":\"frsq_295\",\"name\":\"Bookstore\"},{\"id\":\"frsq_303\",\"name\":\"Boutique\"},{\"id\":\"frsq_5\",\"name\":\"Bowling Alley\"},{\"id\":\"frsq_93\",\"name\":\"Brazilian Restaurant\"},{\"id\":\"frsq_94\",\"name\":\"Breakfast Spot\"},{\"id\":\"frsq_95\",\"name\":\"Brewery\"},{\"id\":\"frsq_296\",\"name\":\"Bridal Shop\"},{\"id\":\"frsq_200\",\"name\":\"Bridge\"},{\"id\":\"frsq_236\",\"name\":\"Building\"},{\"id\":\"frsq_96\",\"name\":\"Burger Joint\"},{\"id\":\"frsq_97\",\"name\":\"Burrito Place\"},{\"id\":\"frsq_385\",\"name\":\"Bus Line\"},{\"id\":\"frsq_384\",\"name\":\"Bus Station\"},{\"id\":\"frsq_322\",\"name\":\"Butcher\"},{\"id\":\"frsq_262\",\"name\":\"Cafeteria\"},{\"id\":\"frsq_98\",\"name\":\"Café\"},{\"id\":\"frsq_99\",\"name\":\"Cajun / Creole Restaurant\"},{\"id\":\"frsq_297\",\"name\":\"Camera Store\"},{\"id\":\"frsq_263\",\"name\":\"Campaign Office\"},{\"id\":\"frsq_201\",\"name\":\"Campground\"},{\"id\":\"frsq_298\",\"name\":\"Candy Store\"},{\"id\":\"frsq_244\",\"name\":\"Capitol Building\"},{\"id\":\"frsq_299\",\"name\":\"Car Dealership\"},{\"id\":\"frsq_300\",\"name\":\"Car Wash\"},{\"id\":\"frsq_100\",\"name\":\"Caribbean Restaurant\"},{\"id\":\"frsq_6\",\"name\":\"Casino\"},{\"id\":\"frsq_202\",\"name\":\"Cemetery\"},{\"id\":\"frsq_323\",\"name\":\"Cheese Shop\"},{\"id\":\"frsq_101\",\"name\":\"Chinese Restaurant\"},{\"id\":\"frsq_277\",\"name\":\"Church\"},{\"id\":\"frsq_245\",\"name\":\"City Hall\"},{\"id\":\"frsq_337\",\"name\":\"Climbing Gym\"},{\"id\":\"frsq_301\",\"name\":\"Clothing Store\"},{\"id\":\"frsq_170\",\"name\":\"Cocktail Bar\"},{\"id\":\"frsq_102\",\"name\":\"Coffee Shop\"},{\"id\":\"frsq_43\",\"name\":\"College & University\"},{\"id\":\"frsq_44\",\"name\":\"College Academic Building\"},{\"id\":\"frsq_52\",\"name\":\"College Administrative Building\"},{\"id\":\"frsq_45\",\"name\":\"College Arts Building\"},{\"id\":\"frsq_53\",\"name\":\"College Auditorium\"},{\"id\":\"frsq_64\",\"name\":\"College Baseball Diamond\"},{\"id\":\"frsq_65\",\"name\":\"College Basketball Court\"},{\"id\":\"frsq_54\",\"name\":\"College Bookstore\"},{\"id\":\"frsq_55\",\"name\":\"College Cafeteria\"},{\"id\":\"frsq_56\",\"name\":\"College Classroom\"},{\"id\":\"frsq_46\",\"name\":\"College Communications Building\"},{\"id\":\"frsq_66\",\"name\":\"College Cricket Pitch\"},{\"id\":\"frsq_47\",\"name\":\"College Engineering Building\"},{\"id\":\"frsq_67\",\"name\":\"College Football Field\"},{\"id\":\"frsq_57\",\"name\":\"College Gym\"},{\"id\":\"frsq_48\",\"name\":\"College History Building\"},{\"id\":\"frsq_68\",\"name\":\"College Hockey Rink\"},{\"id\":\"frsq_58\",\"name\":\"College Lab\"},{\"id\":\"frsq_59\",\"name\":\"College Library\"},{\"id\":\"frsq_49\",\"name\":\"College Math Building\"},{\"id\":\"frsq_60\",\"name\":\"College Quad\"},{\"id\":\"frsq_61\",\"name\":\"College Rec Center\"},{\"id\":\"frsq_62\",\"name\":\"College Residence Hall\"},{\"id\":\"frsq_50\",\"name\":\"College Science Building\"},{\"id\":\"frsq_69\",\"name\":\"College Soccer Field\"},{\"id\":\"frsq_63\",\"name\":\"College Stadium\"},{\"id\":\"frsq_51\",\"name\":\"College Technology Building\"},{\"id\":\"frsq_70\",\"name\":\"College Tennis Court\"},{\"id\":\"frsq_72\",\"name\":\"College Theater\"},{\"id\":\"frsq_71\",\"name\":\"College Track\"},{\"id\":\"frsq_7\",\"name\":\"Comedy Club\"},{\"id\":\"frsq_73\",\"name\":\"Community College\"},{\"id\":\"frsq_8\",\"name\":\"Concert Hall\"},{\"id\":\"frsq_264\",\"name\":\"Conference Room\"},{\"id\":\"frsq_309\",\"name\":\"Convenience Store\"},{\"id\":\"frsq_237\",\"name\":\"Convention Center\"},{\"id\":\"frsq_310\",\"name\":\"Cosmetics Shop\"},{\"id\":\"frsq_246\",\"name\":\"Courthouse\"},{\"id\":\"frsq_265\",\"name\":\"Coworking Space\"},{\"id\":\"frsq_311\",\"name\":\"Credit Union\"},{\"id\":\"frsq_34\",\"name\":\"Cricket Ground\"},{\"id\":\"frsq_103\",\"name\":\"Cuban Restaurant\"},{\"id\":\"frsq_104\",\"name\":\"Cupcake Shop\"},{\"id\":\"frsq_24\",\"name\":\"Dance Studio\"},{\"id\":\"frsq_312\",\"name\":\"Daycare\"},{\"id\":\"frsq_105\",\"name\":\"Deli / Bodega\"},{\"id\":\"frsq_252\",\"name\":\"Dentist's Office\"},{\"id\":\"frsq_313\",\"name\":\"Department Store\"},{\"id\":\"frsq_314\",\"name\":\"Design Studio\"},{\"id\":\"frsq_106\",\"name\":\"Dessert Shop\"},{\"id\":\"frsq_107\",\"name\":\"Dim Sum Restaurant\"},{\"id\":\"frsq_108\",\"name\":\"Diner\"},{\"id\":\"frsq_109\",\"name\":\"Distillery\"},{\"id\":\"frsq_171\",\"name\":\"Dive Bar\"},{\"id\":\"frsq_253\",\"name\":\"Doctor's Office\"},{\"id\":\"frsq_203\",\"name\":\"Dog Run\"},{\"id\":\"frsq_110\",\"name\":\"Donut Shop\"},{\"id\":\"frsq_315\",\"name\":\"Drugstore / Pharmacy\"},{\"id\":\"frsq_111\",\"name\":\"Dumpling Restaurant\"},{\"id\":\"frsq_316\",\"name\":\"EV Charging Station\"},{\"id\":\"frsq_112\",\"name\":\"Eastern European Restaurant\"},{\"id\":\"frsq_317\",\"name\":\"Electronics Store\"},{\"id\":\"frsq_271\",\"name\":\"Elementary School\"},{\"id\":\"frsq_386\",\"name\":\"Embassy / Consulate\"},{\"id\":\"frsq_254\",\"name\":\"Emergency Room\"},{\"id\":\"frsq_113\",\"name\":\"Ethiopian Restaurant\"},{\"id\":\"frsq_239\",\"name\":\"Event Space\"},{\"id\":\"frsq_240\",\"name\":\"Factory\"},{\"id\":\"frsq_241\",\"name\":\"Fair\"},{\"id\":\"frsq_114\",\"name\":\"Falafel Restaurant\"},{\"id\":\"frsq_204\",\"name\":\"Farm\"},{\"id\":\"frsq_324\",\"name\":\"Farmers Market\"},{\"id\":\"frsq_115\",\"name\":\"Fast Food Restaurant\"},{\"id\":\"frsq_387\",\"name\":\"Ferry\"},{\"id\":\"frsq_205\",\"name\":\"Field\"},{\"id\":\"frsq_116\",\"name\":\"Filipino Restaurant\"},{\"id\":\"frsq_318\",\"name\":\"Financial or Legal Service\"},{\"id\":\"frsq_247\",\"name\":\"Fire Station\"},{\"id\":\"frsq_117\",\"name\":\"Fish & Chips Shop\"},{\"id\":\"frsq_325\",\"name\":\"Fish Market\"},{\"id\":\"frsq_319\",\"name\":\"Flea Market\"},{\"id\":\"frsq_320\",\"name\":\"Flower Shop\"},{\"id\":\"frsq_82\",\"name\":\"Food\"},{\"id\":\"frsq_321\",\"name\":\"Food & Drink Shop\"},{\"id\":\"frsq_326\",\"name\":\"Food Court\"},{\"id\":\"frsq_118\",\"name\":\"Food Truck\"},{\"id\":\"frsq_35\",\"name\":\"Football Stadium\"},{\"id\":\"frsq_74\",\"name\":\"Fraternity House\"},{\"id\":\"frsq_119\",\"name\":\"French Restaurant\"},{\"id\":\"frsq_120\",\"name\":\"Fried Chicken Joint\"},{\"id\":\"frsq_242\",\"name\":\"Funeral Home\"},{\"id\":\"frsq_331\",\"name\":\"Furniture / Home Store\"},{\"id\":\"frsq_332\",\"name\":\"Gaming Cafe\"},{\"id\":\"frsq_206\",\"name\":\"Garden\"},{\"id\":\"frsq_333\",\"name\":\"Garden Center\"},{\"id\":\"frsq_334\",\"name\":\"Gas Station / Garage\"},{\"id\":\"frsq_121\",\"name\":\"Gastropub\"},{\"id\":\"frsq_172\",\"name\":\"Gay Bar\"},{\"id\":\"frsq_75\",\"name\":\"General College & University\"},{\"id\":\"frsq_9\",\"name\":\"General Entertainment\"},{\"id\":\"frsq_390\",\"name\":\"General Travel\"},{\"id\":\"frsq_122\",\"name\":\"German Restaurant\"},{\"id\":\"frsq_335\",\"name\":\"Gift Shop\"},{\"id\":\"frsq_123\",\"name\":\"Gluten-free Restaurant\"},{\"id\":\"frsq_190\",\"name\":\"Golf Course\"},{\"id\":\"frsq_327\",\"name\":\"Gourmet Shop\"},{\"id\":\"frsq_243\",\"name\":\"Government Building\"},{\"id\":\"frsq_124\",\"name\":\"Greek Restaurant\"},{\"id\":\"frsq_328\",\"name\":\"Grocery Store\"},{\"id\":\"frsq_339\",\"name\":\"Gym\"},{\"id\":\"frsq_336\",\"name\":\"Gym / Fitness Center\"},{\"id\":\"frsq_338\",\"name\":\"Gym Pool\"},{\"id\":\"frsq_207\",\"name\":\"Harbor / Marina\"},{\"id\":\"frsq_343\",\"name\":\"Hardware Store\"},{\"id\":\"frsq_272\",\"name\":\"High School\"},{\"id\":\"frsq_208\",\"name\":\"Hiking Trail\"},{\"id\":\"frsq_10\",\"name\":\"Historic Site\"},{\"id\":\"frsq_16\",\"name\":\"History Museum\"},{\"id\":\"frsq_344\",\"name\":\"Hobby Shop\"},{\"id\":\"frsq_36\",\"name\":\"Hockey Arena\"},{\"id\":\"frsq_191\",\"name\":\"Hockey Field\"},{\"id\":\"frsq_285\",\"name\":\"Home (private)\"},{\"id\":\"frsq_173\",\"name\":\"Hookah Bar\"},{\"id\":\"frsq_255\",\"name\":\"Hospital\"},{\"id\":\"frsq_394\",\"name\":\"Hostel\"},{\"id\":\"frsq_125\",\"name\":\"Hot Dog Joint\"},{\"id\":\"frsq_209\",\"name\":\"Hot Spring\"},{\"id\":\"frsq_391\",\"name\":\"Hotel\"},{\"id\":\"frsq_174\",\"name\":\"Hotel Bar\"},{\"id\":\"frsq_395\",\"name\":\"Hotel Pool\"},{\"id\":\"frsq_286\",\"name\":\"Housing Development\"},{\"id\":\"frsq_126\",\"name\":\"Ice Cream Shop\"},{\"id\":\"frsq_127\",\"name\":\"Indian Restaurant\"},{\"id\":\"frsq_12\",\"name\":\"Indie Movie Theater\"},{\"id\":\"frsq_25\",\"name\":\"Indie Theater\"},{\"id\":\"frsq_128\",\"name\":\"Indonesian Restaurant\"},{\"id\":\"frsq_345\",\"name\":\"Internet Cafe\"},{\"id\":\"frsq_129\",\"name\":\"Italian Restaurant\"},{\"id\":\"frsq_130\",\"name\":\"Japanese Restaurant\"},{\"id\":\"frsq_20\",\"name\":\"Jazz Club\"},{\"id\":\"frsq_346\",\"name\":\"Jewelry Store\"},{\"id\":\"frsq_131\",\"name\":\"Juice Bar\"},{\"id\":\"frsq_175\",\"name\":\"Karaoke Bar\"},{\"id\":\"frsq_304\",\"name\":\"Kids Store\"},{\"id\":\"frsq_132\",\"name\":\"Korean Restaurant\"},{\"id\":\"frsq_256\",\"name\":\"Laboratory\"},{\"id\":\"frsq_210\",\"name\":\"Lake\"},{\"id\":\"frsq_133\",\"name\":\"Latin American Restaurant\"},{\"id\":\"frsq_347\",\"name\":\"Laundry Service\"},{\"id\":\"frsq_76\",\"name\":\"Law School\"},{\"id\":\"frsq_250\",\"name\":\"Library\"},{\"id\":\"frsq_399\",\"name\":\"Light Rail\"},{\"id\":\"frsq_211\",\"name\":\"Lighthouse\"},{\"id\":\"frsq_305\",\"name\":\"Lingerie Store\"},{\"id\":\"frsq_329\",\"name\":\"Liquor Store\"},{\"id\":\"frsq_176\",\"name\":\"Lounge\"},{\"id\":\"frsq_134\",\"name\":\"Mac & Cheese Joint\"},{\"id\":\"frsq_135\",\"name\":\"Malaysian Restaurant\"},{\"id\":\"frsq_348\",\"name\":\"Mall\"},{\"id\":\"frsq_340\",\"name\":\"Martial Arts Dojo\"},{\"id\":\"frsq_77\",\"name\":\"Mediacl School\"},{\"id\":\"frsq_251\",\"name\":\"Medical Center\"},{\"id\":\"frsq_136\",\"name\":\"Mediterranean Restaurant\"},{\"id\":\"frsq_238\",\"name\":\"Meeting Room\"},{\"id\":\"frsq_306\",\"name\":\"Men's Store\"},{\"id\":\"frsq_273\",\"name\":\"Middle School\"},{\"id\":\"frsq_259\",\"name\":\"Military Base\"},{\"id\":\"frsq_349\",\"name\":\"Miscellaneous Shop\"},{\"id\":\"frsq_350\",\"name\":\"Mobile Phone Shop\"},{\"id\":\"frsq_137\",\"name\":\"Molecular Gastronomy Restaurant\"},{\"id\":\"frsq_138\",\"name\":\"Mongolian Restaurant\"},{\"id\":\"frsq_248\",\"name\":\"Monument / Landmark\"},{\"id\":\"frsq_139\",\"name\":\"Moroccan Restaurant\"},{\"id\":\"frsq_278\",\"name\":\"Mosque\"},{\"id\":\"frsq_396\",\"name\":\"Motel\"},{\"id\":\"frsq_351\",\"name\":\"Motorcycle Shop\"},{\"id\":\"frsq_212\",\"name\":\"Mountain\"},{\"id\":\"frsq_11\",\"name\":\"Movie Theater\"},{\"id\":\"frsq_400\",\"name\":\"Moving Target\"},{\"id\":\"frsq_13\",\"name\":\"Multiplex\"},{\"id\":\"frsq_14\",\"name\":\"Museum\"},{\"id\":\"frsq_274\",\"name\":\"Music School\"},{\"id\":\"frsq_352\",\"name\":\"Music Store\"},{\"id\":\"frsq_19\",\"name\":\"Music Venue\"},{\"id\":\"frsq_353\",\"name\":\"Nail Salon\"},{\"id\":\"frsq_213\",\"name\":\"Neighbourhood\"},{\"id\":\"frsq_140\",\"name\":\"New American Restaurant\"},{\"id\":\"frsq_354\",\"name\":\"Newsstand\"},{\"id\":\"frsq_177\",\"name\":\"Nightclub\"},{\"id\":\"frsq_167\",\"name\":\"Nightlife Spot\"},{\"id\":\"frsq_260\",\"name\":\"Non-Profit\"},{\"id\":\"frsq_275\",\"name\":\"Nursery School\"},{\"id\":\"frsq_261\",\"name\":\"Office\"},{\"id\":\"frsq_26\",\"name\":\"Opera House\"},{\"id\":\"frsq_257\",\"name\":\"Optical Shop\"},{\"id\":\"frsq_214\",\"name\":\"Other Great Outdoors\"},{\"id\":\"frsq_178\",\"name\":\"Other Nightlife\"},{\"id\":\"frsq_186\",\"name\":\"Outdoors & Recreation\"},{\"id\":\"frsq_408\",\"name\":\"PLatform\"},{\"id\":\"frsq_155\",\"name\":\"Paella Restaurant\"},{\"id\":\"frsq_192\",\"name\":\"Paintball Field\"},{\"id\":\"frsq_355\",\"name\":\"Paper / Office Supplies Store\"},{\"id\":\"frsq_215\",\"name\":\"Park\"},{\"id\":\"frsq_267\",\"name\":\"Parking\"},{\"id\":\"frsq_23\",\"name\":\"Performing Arts Venue\"},{\"id\":\"frsq_141\",\"name\":\"Peruvian Restaurant\"},{\"id\":\"frsq_356\",\"name\":\"Pet Service\"},{\"id\":\"frsq_357\",\"name\":\"Pet Store\"},{\"id\":\"frsq_358\",\"name\":\"Photography Lab\"},{\"id\":\"frsq_21\",\"name\":\"Piano Bar\"},{\"id\":\"frsq_389\",\"name\":\"Pier\"},{\"id\":\"frsq_142\",\"name\":\"Pizza Place\"},{\"id\":\"frsq_382\",\"name\":\"Plane\"},{\"id\":\"frsq_17\",\"name\":\"Planetarium\"},{\"id\":\"frsq_216\",\"name\":\"Playground\"},{\"id\":\"frsq_217\",\"name\":\"Plaza\"},{\"id\":\"frsq_249\",\"name\":\"Police Station\"},{\"id\":\"frsq_218\",\"name\":\"Pool\"},{\"id\":\"frsq_28\",\"name\":\"Pool Hall\"},{\"id\":\"frsq_143\",\"name\":\"Portuguese Restaurant\"},{\"id\":\"frsq_268\",\"name\":\"Post Office\"},{\"id\":\"frsq_233\",\"name\":\"Professional & Other Places\"},{\"id\":\"frsq_179\",\"name\":\"Pub\"},{\"id\":\"frsq_29\",\"name\":\"Public Art\"},{\"id\":\"frsq_30\",\"name\":\"Racetrack\"},{\"id\":\"frsq_269\",\"name\":\"Radio Station\"},{\"id\":\"frsq_144\",\"name\":\"Ramen / Noodle House\"},{\"id\":\"frsq_359\",\"name\":\"Real Estate Office\"},{\"id\":\"frsq_360\",\"name\":\"Record Shop\"},{\"id\":\"frsq_361\",\"name\":\"Recycling Facility\"},{\"id\":\"frsq_401\",\"name\":\"Rental Car Location\"},{\"id\":\"frsq_283\",\"name\":\"Residence\"},{\"id\":\"frsq_287\",\"name\":\"Residential Building (Apartment / C...)\"},{\"id\":\"frsq_397\",\"name\":\"Resort\"},{\"id\":\"frsq_402\",\"name\":\"Rest Area\"},{\"id\":\"frsq_145\",\"name\":\"Restaurant\"},{\"id\":\"frsq_219\",\"name\":\"River\"},{\"id\":\"frsq_403\",\"name\":\"Road\"},{\"id\":\"frsq_220\",\"name\":\"Rock Climbing Spot\"},{\"id\":\"frsq_22\",\"name\":\"Rock Club\"},{\"id\":\"frsq_398\",\"name\":\"Roof Deck\"},{\"id\":\"frsq_180\",\"name\":\"Sake Bar\"},{\"id\":\"frsq_146\",\"name\":\"Salad Place\"},{\"id\":\"frsq_362\",\"name\":\"Salon / Barbershop\"},{\"id\":\"frsq_147\",\"name\":\"Sandwich Place\"},{\"id\":\"frsq_148\",\"name\":\"Scandinavian Restaurant\"},{\"id\":\"frsq_221\",\"name\":\"Scenic Lookout\"},{\"id\":\"frsq_270\",\"name\":\"School\"},{\"id\":\"frsq_18\",\"name\":\"Science Museum\"},{\"id\":\"frsq_222\",\"name\":\"Sculpture Garden\"},{\"id\":\"frsq_149\",\"name\":\"Seafood Restaurant\"},{\"id\":\"frsq_307\",\"name\":\"Shoe Store\"},{\"id\":\"frsq_288\",\"name\":\"Shop & Service\"},{\"id\":\"frsq_279\",\"name\":\"Shrine\"},{\"id\":\"frsq_193\",\"name\":\"Skate Park\"},{\"id\":\"frsq_194\",\"name\":\"Skating Rink\"},{\"id\":\"frsq_223\",\"name\":\"Ski Area\"},{\"id\":\"frsq_225\",\"name\":\"Ski Chairlift\"},{\"id\":\"frsq_226\",\"name\":\"Ski Chalet\"},{\"id\":\"frsq_227\",\"name\":\"Ski Lodge\"},{\"id\":\"frsq_228\",\"name\":\"Ski Trail\"},{\"id\":\"frsq_363\",\"name\":\"Smoke Shop\"},{\"id\":\"frsq_150\",\"name\":\"Snack Place\"},{\"id\":\"frsq_195\",\"name\":\"Soccer Field\"},{\"id\":\"frsq_37\",\"name\":\"Soccer Stadium\"},{\"id\":\"frsq_78\",\"name\":\"Sorority House\"},{\"id\":\"frsq_151\",\"name\":\"Soup Place\"},{\"id\":\"frsq_152\",\"name\":\"South American Restaurant\"},{\"id\":\"frsq_153\",\"name\":\"Southern / Soul Food Restaurant\"},{\"id\":\"frsq_364\",\"name\":\"Spa / Massage\"},{\"id\":\"frsq_154\",\"name\":\"Spanish Restaurant\"},{\"id\":\"frsq_181\",\"name\":\"Speakeasy\"},{\"id\":\"frsq_276\",\"name\":\"Spiritual Center\"},{\"id\":\"frsq_365\",\"name\":\"Sporting Goods Shop\"},{\"id\":\"frsq_182\",\"name\":\"Sports Bar\"},{\"id\":\"frsq_229\",\"name\":\"Stable\"},{\"id\":\"frsq_31\",\"name\":\"Stadium\"},{\"id\":\"frsq_156\",\"name\":\"Steakhouse\"},{\"id\":\"frsq_366\",\"name\":\"Storage Facility\"},{\"id\":\"frsq_183\",\"name\":\"Strip Club\"},{\"id\":\"frsq_79\",\"name\":\"Student Center\"},{\"id\":\"frsq_404\",\"name\":\"Subway\"},{\"id\":\"frsq_199\",\"name\":\"Surf Spot\"},{\"id\":\"frsq_157\",\"name\":\"Sushi Restaurant\"},{\"id\":\"frsq_158\",\"name\":\"Swiss Restaurant\"},{\"id\":\"frsq_280\",\"name\":\"Synagogue\"},{\"id\":\"frsq_159\",\"name\":\"Taco Place\"},{\"id\":\"frsq_367\",\"name\":\"Tailor Shop\"},{\"id\":\"frsq_368\",\"name\":\"Tanning Salon\"},{\"id\":\"frsq_160\",\"name\":\"Tapas Restaurant\"},{\"id\":\"frsq_369\",\"name\":\"Tattoo Parlor\"},{\"id\":\"frsq_405\",\"name\":\"Taxi\"},{\"id\":\"frsq_161\",\"name\":\"Tea Room\"},{\"id\":\"frsq_266\",\"name\":\"Tech Startup\"},{\"id\":\"frsq_281\",\"name\":\"Temple\"},{\"id\":\"frsq_38\",\"name\":\"Tennis\"},{\"id\":\"frsq_196\",\"name\":\"Tennis Court\"},{\"id\":\"frsq_162\",\"name\":\"Thai Restaurant\"},{\"id\":\"frsq_27\",\"name\":\"Theater\"},{\"id\":\"frsq_40\",\"name\":\"Theme Park\"},{\"id\":\"frsq_370\",\"name\":\"Thrift / Vintage Store\"},{\"id\":\"frsq_406\",\"name\":\"Tourist Information Center\"},{\"id\":\"frsq_371\",\"name\":\"Toy / Game Store\"},{\"id\":\"frsq_341\",\"name\":\"Track\"},{\"id\":\"frsq_39\",\"name\":\"Track Stadium\"},{\"id\":\"frsq_80\",\"name\":\"Trade School\"},{\"id\":\"frsq_409\",\"name\":\"Train\"},{\"id\":\"frsq_407\",\"name\":\"Train Station\"},{\"id\":\"frsq_375\",\"name\":\"Travel & Transport\"},{\"id\":\"frsq_372\",\"name\":\"Travel Agency\"},{\"id\":\"frsq_410\",\"name\":\"Travel Lounge\"},{\"id\":\"frsq_81\",\"name\":\"University\"},{\"id\":\"frsq_163\",\"name\":\"Vegetarian / Vegan Restaurant\"},{\"id\":\"frsq_258\",\"name\":\"Veterinarian\"},{\"id\":\"frsq_373\",\"name\":\"Video Game Store\"},{\"id\":\"frsq_374\",\"name\":\"Video Store\"},{\"id\":\"frsq_164\",\"name\":\"Vietnamese Restaurant\"},{\"id\":\"frsq_230\",\"name\":\"Vineyard\"},{\"id\":\"frsq_231\",\"name\":\"Volcano\"},{\"id\":\"frsq_197\",\"name\":\"Volleyball Court\"},{\"id\":\"frsq_282\",\"name\":\"Voting Booth\"},{\"id\":\"frsq_41\",\"name\":\"Water Park\"},{\"id\":\"frsq_232\",\"name\":\"Well\"},{\"id\":\"frsq_184\",\"name\":\"Whisky Bar\"},{\"id\":\"frsq_185\",\"name\":\"Wine Bar\"},{\"id\":\"frsq_330\",\"name\":\"Wine Shop\"},{\"id\":\"frsq_165\",\"name\":\"Winery\"},{\"id\":\"frsq_166\",\"name\":\"Wings Joint\"},{\"id\":\"frsq_308\",\"name\":\"Women's Store\"},{\"id\":\"frsq_342\",\"name\":\"Yoga Studio\"},{\"id\":\"frsq_42\",\"name\":\"Zoo\"}]}";
			//				output = PlaceRetriever.getPlaceCategories();
			return Response.status(200).entity(output).build();
		}   catch (NullPointerException e) {
			// TODO
			e.printStackTrace();
			return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"No place categories found.\"}").build();
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
			return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"No place categories found.\"}").build();
		}

	}

	@GET
	@Path("/products/")
	@Produces("application/json")
	@ApiOperation(value = "Get product recommendations",httpMethod="GET", notes = "Get recommended products for OPENi user from specific store.")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "Service not found") 
	})
	public Response getProducts(
			@ApiParam(value="algorithmSortingParameter",required=false, name="sortParam", allowableValues = "count,sum,mean")@DefaultValue("mean") @QueryParam("sortParam") String sortParam,
			@ApiParam(value="product category",required=false, name="category")@DefaultValue("all") @QueryParam("category") String category,
			@ApiParam(value="criteria used to order recommendations",required=false, name="orderby",allowableValues = "score,name,price")@DefaultValue("score") @QueryParam("orderby") String orderby,
			@ApiParam(value="user context to be taken into consideration",required=true, name="context",allowableValues = "all,gender,age,education,interests,country,ethnicity,family_status",allowMultiple=true)@DefaultValue("all") @QueryParam("context") String contextTypes,
			@ApiParam(value="context id",required=true, name="id")@DefaultValue("1") @QueryParam("id") String auth, 
			@ApiParam(value="starting point of the price",required=false, name="priceLower")@DefaultValue("1") @QueryParam("priceLower") int priceLow, 
			@ApiParam(value="upper limit of the price",required=false, name="priceUpper")@DefaultValue("1") @QueryParam("priceUpper") int priceUp, 
			@ApiParam(value="currency to apply on price filters-currently only euro",required=false, name="currency", allowableValues="euro")@DefaultValue("euro") @QueryParam("currency") String currency) {

		String output;
		boolean useInterests = false;
		boolean useFamily = false;
		boolean useAge = false;
		boolean useGender = false;
		boolean useEducation = false;
		boolean useCategs = false;

		if (!category.equals("all"))
			useCategs = true;
		System.out.println(category);

		if (contextTypes.contains("all")) {
			useInterests = true;
			useAge = true;
			useEducation = true;
			useGender = true;
			useFamily = true;
		}
		if (contextTypes.contains("gender")) {
			useGender = true;
		}
		if (contextTypes.contains("age")) {
			useAge = true;
		}
		if (contextTypes.contains("education")) {
			useEducation = true;
		}

		if (contextTypes.contains("interests")) {
			useInterests = true;
		}
		if (contextTypes.contains("family_status")) {
			useFamily = true;
		}


		ContextPermissions permissions = new ContextPermissions(useInterests, useAge, useEducation, useGender, false, false, false, useFamily, useFamily, false);




		try {
			System.out.println(auth);
			if (useCategs)
				output = ProductRecommender.getRecommendation(auth,permissions,sortParam,category);
			else
				output = ProductRecommender.getRecommendation(auth,permissions,sortParam);
			return Response.status(200).entity(output).build();
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
			return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"Product recommendation service not found.\"}").build();
		}

	}

	@GET
	@Path("/products/categories/")
	@Produces("application/json")
	@ApiOperation(value = "Get product categories",httpMethod="GET", notes = "You can find here the available categories for OPENi products.Currently shows dummy data.")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "No product categories found") 
	})
	public Response getProductsCategories(
			) {

		try{

			//			String output = ProductsRetriever.getProductCategories();
			String output = "{\"categories\":[{\"id\":\"70000000\",\"name\":\"Arts - Crafts - Needlework\"},{\"id\":\"68000000\",\"name\":\"Audio Visual - Photography\"},{\"id\":\"77000000\",\"name\":\"Automotive\"},{\"id\":\"54000000\",\"name\":\"Baby Care\"},{\"id\":\"53000000\",\"name\":\"Beauty - Personal Care - Hygiene\"},{\"id\":\"83000000\",\"name\":\"Building Products\"},{\"id\":\"47000000\",\"name\":\"Cleaning - Hygiene Products\"},{\"id\":\"67000000\",\"name\":\"Clothing\"},{\"id\":\"66000000\",\"name\":\"Communications\"},{\"id\":\"65000000\",\"name\":\"Computing\"},{\"id\":\"58000000\",\"name\":\"Cross Segment\"},{\"id\":\"78000000\",\"name\":\"Electrical Supplies\"},{\"id\":\"50000000\",\"name\":\"Food - Beverage - Tobacco\"},{\"id\":\"63000000\",\"name\":\"Footwear\"},{\"id\":\"51000000\",\"name\":\"Healthcare\"},{\"id\":\"72000000\",\"name\":\"Home Appliances\"},{\"id\":\"75000000\",\"name\":\"Household - Office Furniture - Furnishings\"},{\"id\":\"73000000\",\"name\":\"Kitchen Merchandise\"},{\"id\":\"81000000\",\"name\":\"Lawn - Garden Supplies\"},{\"id\":\"88000000\",\"name\":\"Lubricants\"},{\"id\":\"61000000\",\"name\":\"Music\"},{\"id\":\"64000000\",\"name\":\"Personal Accessories\"},{\"id\":\"10000000\",\"name\":\"Pet Care - Food\"},{\"id\":\"79000000\",\"name\":\"Plumbing - Heating - Ventilation - Air Conditioning\"},{\"id\":\"85000000\",\"name\":\"Safety - Protection - DIY\"},{\"id\":\"71000000\",\"name\":\"Sports Equipment\"},{\"id\":\"62000000\",\"name\":\"Stationery - Office Machinery - Occasion Supplies\"},{\"id\":\"84000000\",\"name\":\"Tool Storage - Workshop Aids\"},{\"id\":\"80000000\",\"name\":\"Tools - Equipment - Hand\"},{\"id\":\"82000000\",\"name\":\"Tools - Equipment - Power\"},{\"id\":\"86000000\",\"name\":\"Toys - Games\"},{\"id\":\"gpc_s_cd\",\"name\":\"gpc_s_nm\"}]}";

			return Response.status(200).entity(output).build();
		}
		catch (Exception e){
			return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"No product categories found.\"}").build();

		}
	}

	@GET
	@Path("/applications/")
	@Produces("application/json")
	@ApiOperation(value = "Get application recommendations",httpMethod="GET", notes = "Get recommended applications for OPENi user.")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "Application recommendation service not found") 
	})
	public Response getApplications(
			@ApiParam(value="user id - temporary",required=true, name="id")@DefaultValue("1") @QueryParam("id") int id, 
			@ApiParam(value="application category",required=false, name="category")@DefaultValue("all") @QueryParam("category") String category,
			@ApiParam(value="criteria used to order recommendations",required=false, name="orderby",allowableValues = "score,name,price")@DefaultValue("score") @QueryParam("orderby") String orderby,
			@ApiParam(value="user context to be taken into consideration",required=false, name="context",allowableValues = "all,gender,age,education,interests,country,ethnicity,family_status",allowMultiple=true)@DefaultValue("all") @QueryParam("context") List<String> contextTypes){

		String output;


		try {
			output = ApplicationRecommender.getRecommendation("");
			return Response.status(200).entity(output).build();
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
			return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"Application recommendation service not found.\"}").build();
		}

	}

	@GET
	@Path("/applications/categories/")
	@Produces("application/json")
	@ApiOperation(value = "Get the application categories",httpMethod="GET", notes = "You can find here the available categories for OPENi applications.Currently shows dummy data.")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "No application categories found") 
	})
	public Response getApplicationsCategories(
			) {

		try{
			return Response.status(200).entity("{\"categories:\":[{\"name\":\"games\",\"id\":\"ac_1\"},{\"name\":\"entertainment\",\"id\":\"ac_1\"}]}").build();
		}
		catch (Exception e){
			return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"No application categories found.\"}").build();

		}
	}



}