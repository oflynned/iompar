package com.glassbyte.iompar;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.util.Locale;

/**
 * Created by ed on 28/10/15.
 */
public class Globals extends ContextWrapper {
    
    public Globals(Context context){
        super(context);
    }

    //ad revenue IDs
    public static String ADMOB_ID = "ca-app-pub-7053740901071844/4386004018";
    public static String ADMOB_ID_DEVELOPMENT = "ca-app-pub-3940256099942544/1033173712";

    //http querying from RTPI.ie
    public static String RTPI = "http://www.rtpi.ie/Text/WebDisplay.aspx?stopRef=";
    public static String LEAP_LOGIN = "https://www.leapcard.ie/en/Login.aspx";
    public static String LEAP_LOGIN_ACCOUNT_PAGE = "https://www.leapcard.ie/en/SelfServices/CardServices/CardOverView.aspx";
    public static String LUAS_FARES = "https://www.luas.ie/single-and-return-tickets.html";

    //to be implemented on next releases - scraping from DB and Irish Rail
    public static String DUBLIN_BUS_SEARCH_BY_STOP_NUMBER = "http://www.dublinbus.ie/en/RTPI/Sources-of-Real-Time-Information/?searchtype=view&searchquery=";
    public static String DUBLIN_BUS_SEARCH_BY_ROUTE = "http://www.dublinbus.ie/en/RTPI/Sources-of-Real-Time-Information/?searchtype=route&searchquery=";

    //DELETE THIS BEFORE FINAL PUBLISHING -- ONLY FOR EASE OF ACCESS
    public static final String USER_LEAP_NUMBER = "10061949261114";
    public static final String USER_NAME = "oflynned";
    public static final String USER_EMAIL = "oflynned@tcd.ie";
    public static final String USER_PASS = "thuga8Da!";

    //timeouts
    public static final int ONE_SECOND = 1000;
    public static final int TEN_SECONDS = ONE_SECOND * 10;
    public static final int SIXTY_SECONDS = TEN_SECONDS * 6;

    //zone IDs
    public static final int DOCKLANDS_ID = 1;
    public static final int CENTRAL_1_ID = 2;
    public static final int RED_2_ID = 3;
    public static final int RED_3_ID = 4;
    public static final int RED_4_ID = 5;
    public static final int GREEN_2_ID = 3;
    public static final int GREEN_3_ID = 4;
    public static final int GREEN_4_ID = 5;
    public static final int GREEN_5_ID = 6;

    //green line
    public static final int STEPHENS_GREEN_ID = 1;
    public static final int CHARLEMONT_ID = 3;
    public static final int DUNDRUM_ID = 9;
    public static final int SANDYFORD_ID = 13;
    public static final int BALLYOGAN_WOOD_ID = 18;
    public static final int CARRICKMINES_ID = 19;
    public static final int BRIDES_GLEN_ID = 22;

    //saggart-connolly
    public static final int CONNOLLY_SAGGART_ID = 1;
    public static final int HEUSTON_SAGGART_ID = 8;
    public static final int SUIR_ROAD_SAGGART_ID = 12;
    public static final int RED_COW_SAGGART_ID = 18;
    public static final int BELGARD_SAGGART_ID = 20;
    public static final int SAGGART_ID = 25;

    //tallaght-point
    public static final int THE_POINT_TALLAGHT_ID = 1;
    public static final int GEORGES_DOCK_TALLAGHT_ID = 4;
    public static final int BUSARAS_TALLAGHT_ID = 5;
    public static final int HEUSTON_TALLAGHT_ID = 11;
    public static final int SUIR_ROAD_TALLAGHT_ID = 15;
    public static final int RED_COW_TALLAGHT_ID = 21;
    public static final int BELGARD_TALLAGHT_ID = 23;
    public static final int TALLAGHT_ID = 26;

    Locale locale;
    String language;

    //language support
    public void setIrish(boolean isIrishChosen, Resources res){
        if(isIrishChosen){
            setLocale("ga", res);
        } else {
            setLocale(Locale.getDefault().getDisplayLanguage(), res);
        }
    }

    public void setLocale(String language, Resources res){
        this.language = language;
        locale = new Locale(language);
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = locale;
        res.updateConfiguration(conf, dm);
    }

    public String greenLineBeforeSandyford[] = {
            this.getString(R.string.stephens_green),
            this.getString(R.string.harcourt_st),
            this.getString(R.string.charlemont),
            this.getString(R.string.ranelagh),
            this.getString(R.string.beechwood),
            this.getString(R.string.cowper),
            this.getString(R.string.milltown),
            this.getString(R.string.windy_arbour),
            this.getString(R.string.dundrum),
            this.getString(R.string.balally),
            this.getString(R.string.kilmacud),
            this.getString(R.string.stillorgan),
            this.getString(R.string.sandyford)
    };

    public String[] greenLineStationsBridesGlenStephensGreen = {
            this.getString(R.string.stephens_green),
            this.getString(R.string.harcourt_st),
            this.getString(R.string.charlemont),
            this.getString(R.string.ranelagh),
            this.getString(R.string.beechwood),
            this.getString(R.string.cowper),
            this.getString(R.string.milltown),
            this.getString(R.string.windy_arbour),
            this.getString(R.string.dundrum),
            this.getString(R.string.balally),
            this.getString(R.string.kilmacud),
            this.getString(R.string.stillorgan),
            this.getString(R.string.sandyford),
            this.getString(R.string.central_park),
            this.getString(R.string.glencairn),
            this.getString(R.string.the_gallops),
            this.getString(R.string.leopardstown_valley),
            this.getString(R.string.ballyogan_wood),
            this.getString(R.string.carrickmines),
            this.getString(R.string.laughanstown),
            this.getString(R.string.cherrywood),
            this.getString(R.string.brides_glen)
    };

    public String[] redLineStationsSaggartConnolly = {
            this.getString(R.string.connolly),
            this.getString(R.string.busaras),
            this.getString(R.string.abbey_street),
            this.getString(R.string.jervis),
            this.getString(R.string.four_courts),
            this.getString(R.string.smithfield),
            this.getString(R.string.museum),
            this.getString(R.string.heuston),
            this.getString(R.string.jamess),
            this.getString(R.string.fatima),
            this.getString(R.string.rialto),
            this.getString(R.string.suir_road),
            this.getString(R.string.goldenbridge),
            this.getString(R.string.drimnagh),
            this.getString(R.string.blackhorse),
            this.getString(R.string.bluebell),
            this.getString(R.string.kylemore),
            this.getString(R.string.red_cow),
            this.getString(R.string.kingswood),
            this.getString(R.string.belgard),
            this.getString(R.string.fettercairn),
            this.getString(R.string.cheeverstown),
            this.getString(R.string.citywest_campus),
            this.getString(R.string.fortunestown),
            this.getString(R.string.saggart)
    };

    public String[] redLineStationsHeustonConnolly = {
            this.getString(R.string.connolly),
            this.getString(R.string.busaras),
            this.getString(R.string.abbey_street),
            this.getString(R.string.jervis),
            this.getString(R.string.four_courts),
            this.getString(R.string.smithfield),
            this.getString(R.string.museum),
            this.getString(R.string.heuston)
    };

    public String[]  redLineStationsTallaghtPoint = {
            this.getString(R.string.the_point),
            this.getString(R.string.spencer_dock),
            this.getString(R.string.mayor_square_nci),
            this.getString(R.string.georges_dock),
            this.getString(R.string.busaras),
            this.getString(R.string.abbey_street),
            this.getString(R.string.jervis),
            this.getString(R.string.four_courts),
            this.getString(R.string.smithfield),
            this.getString(R.string.museum),
            this.getString(R.string.heuston),
            this.getString(R.string.jamess),
            this.getString(R.string.fatima),
            this.getString(R.string.rialto),
            this.getString(R.string.suir_road),
            this.getString(R.string.goldenbridge),
            this.getString(R.string.drimnagh),
            this.getString(R.string.blackhorse),
            this.getString(R.string.bluebell),
            this.getString(R.string.kylemore),
            this.getString(R.string.red_cow),
            this.getString(R.string.kingswood),
            this.getString(R.string.belgard),
            this.getString(R.string.cookstown),
            this.getString(R.string.hospital),
            this.getString(R.string.tallaght)
    };

    public enum LineDirection {
        the_point_to_tallaght,
        the_point_to_belgard,

        tallaght_to_the_point,
        tallaght_to_belgard,

        saggart_to_belgard,

        belgard_to_the_point,
        belgard_to_connolly,
        belgard_to_saggart,
        belgard_to_tallaght,

        heuston_to_connolly,
        connolly_to_heuston,
        connolly_to_belgard,
        connolly_to_saggart,

        stephens_green_to_brides_glen,
        stephens_green_to_sandyford,

        sandyford_to_stephens_green,
        brides_glen_to_stephens_green
    }

    public String getLuasStation(String string) {
        //green
        if(string.equals(this.getString(R.string.stephens_green))) {
            return "LUAS24";
        } else if(string.equals(this.getString(R.string.harcourt_st))) {
            return "LUAS25";
        } else if(string.equals(this.getString(R.string.charlemont))) {
            return "LUAS26";
        } else if(string.equals(this.getString(R.string.ranelagh))) {
            return "LUAS27";
        } else if(string.equals(this.getString(R.string.beechwood))) {
            return "LUAS28";
        } else if(string.equals(this.getString(R.string.cowper))) {
            return "LUAS29";
        } else if(string.equals(this.getString(R.string.milltown))) {
            return "LUAS30";
        } else if(string.equals(this.getString(R.string.windy_arbour))) {
            return "LUAS31";
        } else if(string.equals(this.getString(R.string.dundrum))) {
            return "LUAS32";
        } else if(string.equals(this.getString(R.string.balally))) {
            return "LUAS33";
        } else if(string.equals(this.getString(R.string.kilmacud))) {
            return "LUAS34";
        } else if(string.equals(this.getString(R.string.stillorgan))) {
            return "LUAS35";
        } else if(string.equals(this.getString(R.string.sandyford))) {
            return "LUAS36";
        } else if(string.equals(this.getString(R.string.central_park))) {
            return "LUAS37";
        } else if(string.equals(this.getString(R.string.glencairn))) {
            return "LUAS38";
        } else if(string.equals(this.getString(R.string.the_gallops))) {
            return "LUAS39";
        } else if(string.equals(this.getString(R.string.leopardstown_valley))) {
            return "LUAS40";
        } else if(string.equals(this.getString(R.string.ballyogan_wood))) {
            return "LUAS42";
        } else if(string.equals(this.getString(R.string.carrickmines))) {
            return "LUAS44";
        } else if(string.equals(this.getString(R.string.laughanstown))) {
            return "LUAS46";
        } else if(string.equals(this.getString(R.string.cherrywood))) {
            return "LUAS47";
        } else if(string.equals(this.getString(R.string.brides_glen))) {
            return "LUAS48";
        }

        //red
        else if(string.equals(this.getString(R.string.the_point))) {
            return "LUAS57";
        } else if(string.equals(this.getString(R.string.spencer_dock))) {
            return "LUAS56";
        } else if(string.equals(this.getString(R.string.mayor_square_nci))) {
            return "LUAS55";
        } else if(string.equals(this.getString(R.string.georges_dock))) {
            return "LUAS54";
        } else if(string.equals(this.getString(R.string.connolly))) {
            return "LUAS23";
        } else if(string.equals(this.getString(R.string.busaras))) {
            return "LUAS22";
        } else if(string.equals(this.getString(R.string.abbey_street))) {
            return "LUAS21";
        } else if(string.equals(this.getString(R.string.jervis))) {
            return "LUAS20";
        } else if(string.equals(this.getString(R.string.four_courts))) {
            return "LUAS19";
        } else if(string.equals(this.getString(R.string.smithfield))) {
            return "LUAS18";
        } else if(string.equals(this.getString(R.string.museum))) {
            return "LUAS17";
        } else if(string.equals(this.getString(R.string.heuston))) {
            return "LUAS16";
        } else if(string.equals(this.getString(R.string.jamess))) {
            return "LUAS15";
        } else if(string.equals(this.getString(R.string.fatima))) {
            return "LUAS14";
        } else if(string.equals(this.getString(R.string.rialto))) {
            return "LUAS13";
        } else if(string.equals(this.getString(R.string.suir_road))) {
            return "LUAS12";
        } else if(string.equals(this.getString(R.string.goldenbridge))) {
            return "LUAS11";
        } else if(string.equals(this.getString(R.string.drimnagh))) {
            return "LUAS10";
        } else if(string.equals(this.getString(R.string.blackhorse))) {
            return "LUAS9";
        } else if(string.equals(this.getString(R.string.bluebell))) {
            return "LUAS8";
        } else if(string.equals(this.getString(R.string.kylemore))) {
            return "LUAS7";
        } else if(string.equals(this.getString(R.string.red_cow))) {
            return "LUAS6";
        } else if(string.equals(this.getString(R.string.kingswood))) {
            return "LUAS5";
        } else if(string.equals(this.getString(R.string.belgard))) {
            return "LUAS4";
        } else if(string.equals(this.getString(R.string.cookstown))) {
            return "LUAS3";
        } else if(string.equals(this.getString(R.string.hospital))) {
            return "LUAS2";
        } else if(string.equals(this.getString(R.string.tallaght))) {
            return "LUAS1";
        } else if(string.equals(this.getString(R.string.fettercairn))) {
            return "LUAS49";
        } else if(string.equals(this.getString(R.string.cheeverstown))) {
            return "LUAS50";
        } else if(string.equals(this.getString(R.string.citywest_campus))) {
            return "LUAS51";
        } else if(string.equals(this.getString(R.string.fortunestown))) {
            return "LUAS52";
        } else if(string.equals(this.getString(R.string.saggart))) {
            return "LUAS53";
        }
        return null;
    }
}