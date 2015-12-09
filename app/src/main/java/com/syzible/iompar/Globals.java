package com.syzible.iompar;

/**
 * Created by ed on 28/10/15.
 */
public class Globals {

    public String tag = "";

    //profile
    String navName = "";
    String navLeapNumber = "";
    boolean loggedIn;

    //http querying
    public static String RTPI = "http://www.rtpi.ie/Text/WebDisplay.aspx?stopRef=";
    public static String LEAP_LOGIN = "https://www.leapcard.ie/en/Login.aspx";
    public static String LEAP_LOGIN_ACCOUNT_PAGE = "https://www.leapcard.ie/en/SelfServices/CardServices/CardOverView.aspx";

    public static final String USER_LEAP_NUMBER = "10061949261114";
    public static final String USER_NAME = "oflynned";
    public static final String USER_EMAIL = "oflynned@tcd.ie";
    public static final String USER_PASS = "thuga8Da!";

    public static final int ONE_SECOND = 1000;
    public static final int TENTH_OF_SECOND = ONE_SECOND / 10;
    public static final int FIVE_SECONDS = ONE_SECOND * 5;
    public static final int TEN_SECONDS = ONE_SECOND * 10;

    //zone IDs
    public static final int DOCKLANDS_ID = 1;
    public static final int CENTRAL_1_ID = 2;
    public static final int RED_2_ID = 3;
    public static final int RED_3_ID = 4;
    public static final int RED_4_ID = 5;
    public static final int GREEN_2_ID = 6;
    public static final int GREEN_3_ID = 7;
    public static final int GREEN_4_ID = 8;
    public static final int GREEN_5_ID = 9;

    //green
    public static final int STEPHENS_GREEN_ID = 0;
    public static final int SANDYFORD_ID = 12;
    public static final int BRIDES_GLEN_ID = 21;

    //interstation red
    public static final int CONNOLLY_ID = 0;
    public static final int HEUSTON_ID = 8;

    //red
    public static final int THE_POINT_ID = 0;
    public static final int BELGARD_ID = 22;
    public static final int TALLAGHT_ID = 25;
    public static final int SAGGART_ID = 27;

    public String docklands[] = {
            "The Point",
            "Spencer Dock",
            "Mayor Square - NCI",
            "George's Dock",
            "Connolly"
    };

    public String central_1[] = {
            "St. Stephen's Green",
            "Harcourt Street",
            "Charlemont",
            "Connolly",
            "George's Dock",
            "Busaras",
            "Abbey Street",
            "Jervis",
            "Four Courts",
            "Smithfield",
            "Museum",
            "Heuston"
    };

    public String red_2[] = {
            "Heuston",
            "James's",
            "Fatima",
            "Rialto",
            "Suir Road"
    };

    public String red_3[] = {
            "Suir Road",
            "Goldenbridge",
            "Drimnagh",
            "Blackhorse",
            "Bluebell",
            "Kylemore",
            "Red Cow"
    };

    public String red_4[] = {
            "Red Cow",
            "Belgard",
            "Cookstown",
            "Hospital",
            "Tallaght",
            "Fettercairn",
            "Cheeverstown",
            "Citywest Campus",
            "Fortunestown",
            "Saggart"
    };

    public String green_2[] = {
            "Charlemont",
            "Ranelagh",
            "Beechwood",
            "Cowper",
            "Milltown",
            "Windy Arbour",
            "Dundrum"
    };

    public String green_3[] = {
            "Dundrum",
            "Balally",
            "Kilmacud",
            "Stillorgan",
            "Sandyford"
    };

    public String green_4[] = {
            "Sandyford",
            "Central Park",
            "Glencairn",
            "The Gallops",
            "Leopardstown Valley",
            "Ballyogan Wood"
    };

    public String green_5[] = {
            "Carrickmines",
            "Laughanstown",
            "Cherrywood",
            "Bride's Glen"
    };

    public String greenLineBeforeSandyford[] = {
            "St. Stephen's Green",
            "Harcourt St.",
            "Charlemont",
            "Ranelagh",
            "Beechwood",
            "Cowper",
            "Milltown",
            "Windy Arbour",
            "Dundrum",
            "Balally",
            "Kilmacud",
            "Stillorgan",
            "Sandyford"
    };

    public String greenLineBeforeBridesGlen[] = {
            "Central Park",
            "Glencairn",
            "The Gallops",
            "Leopardstown Valley",
            "Ballyogan Wood",
            "Carrickmines",
            "Laughanstown",
            "Cherrywood",
            "Bride's Glen"
    };

    public String greenLineStationsBridesGlenStephensGreen[] = {
            "St. Stephen's Green",
            "Harcourt St.",
            "Charlemont",
            "Ranelagh",
            "Beechwood",
            "Cowper",
            "Milltown",
            "Windy Arbour",
            "Dundrum",
            "Balally",
            "Kilmacud",
            "Stillorgan",
            "Sandyford",
            "Central Park",
            "Glencairn",
            "The Gallops",
            "Leopardstown Valley",
            "Ballyogan Wood",
            "Carrickmines",
            "Laughanstown",
            "Cherrywood",
            "Bride's Glen"
    };

    public String greenLineStationsSandyfordStephensGreen[] = {
            "St. Stephen's Green",
            "Harcourt St.",
            "Charlemont",
            "Ranelagh",
            "Beechwood",
            "Cowper",
            "Milltown",
            "Windy Arbour",
            "Dundrum",
            "Balally",
            "Kilmacud",
            "Stillorgan",
            "Sandyford"
    };

    public String redLineStationsSaggartPoint[] = {
            "The Point",
            "Spencer Dock",
            "Mayor Square - NCI",
            "George's Dock",
            "Busaras",
            "Abbey Street",
            "Jervis",
            "Four Courts",
            "Smithfield",
            "Museum",
            "Heuston",
            "James's",
            "Fatima",
            "Rialto",
            "Suir Road",
            "Goldenbridge",
            "Drimnagh",
            "Blackhorse",
            "Bluebell",
            "Kylemore",
            "Red Cow",
            "Kingswood",
            "Belgard",
            "Fettercairn",
            "Cheeverstown",
            "Citywest Campus",
            "Fortunestown",
            "Saggart"
    };

    public String redLineStationsHeustonConnolly[] = {
            "Connolly",
            "Busaras",
            "Abbey Street",
            "Jervis",
            "Four Courts",
            "Smithfield",
            "Museum",
            "Heuston"
    };

    public String redLineStationsTallaghtPoint[] = {
            "The Point",
            "Spencer Dock",
            "Mayor Square - NCI",
            "George's Dock",
            "Busaras",
            "Abbey Street",
            "Jervis",
            "Four Courts",
            "Smithfield",
            "Museum",
            "Heuston",
            "James's",
            "Fatima",
            "Rialto",
            "Suir Road",
            "Goldenbridge",
            "Drimnagh",
            "Blackhorse",
            "Bluebell",
            "Kylemore",
            "Red Cow",
            "Kingswood",
            "Belgard",
            "Cookstown",
            "Hospital",
            "Tallaght"
    };

    public enum Line {
        red,
        green
    }

    public enum LineDirection {
        the_point_to_tallaght,
        the_point_to_saggart,
        the_point_to_belgard,

        tallaght_to_the_point,
        tallaght_to_belgard,

        saggart_to_the_point,
        saggart_to_belgard,
        saggart_to_connolly,

        belgard_to_the_point,
        belgard_to_saggart,
        belgard_to_tallaght,

        heuston_to_connolly,
        connolly_to_heuston,
        connolly_to_saggart,

        stephens_green_to_brides_glen,
        stephens_green_to_sandyford,

        sandyford_to_brides_glen,
        sandyford_to_stephens_green,
        brides_glen_to_sandyford,
        brides_glen_to_stephens_green
    }

    public String getLuasStation(String string) {
        String RTPI = "";
        switch (string) {
            //green
            case "St. Stephen's Green":
                RTPI = "LUAS24";
                break;
            case "Harcourt St.":
                RTPI = "LUAS25";
                break;
            case "Charlemont":
                RTPI = "LUAS26";
                break;
            case "Ranelagh":
                RTPI = "LUAS27";
                break;
            case "Beechwood":
                RTPI = "LUAS28";
                break;
            case "Cowper":
                RTPI = "LUAS29";
                break;
            case "Milltown":
                RTPI = "LUAS30";
                break;
            case "Windy Arbour":
                RTPI = "LUAS31";
                break;
            case "Dundrum":
                RTPI = "LUAS32";
                break;
            case "Balally":
                RTPI = "LUAS33";
                break;
            case "Kilmacud":
                RTPI = "LUAS34";
                break;
            case "Stillorgan":
                RTPI = "LUAS35";
                break;
            case "Sandyford":
                RTPI = "LUAS36";
                break;
            case "Central Park":
                RTPI = "LUAS37";
                break;
            case "Glencairn":
                RTPI = "LUAS38";
                break;
            case "The Gallops":
                RTPI = "LUAS39";
                break;
            case "Leopardstown Valley":
                RTPI = "LUAS40";
                break;
            case "Ballogan Wood":
                RTPI = "LUAS42";
                break;
            case "Carrickmines":
                RTPI = "LUAS44";
                break;
            case "Laughanstown":
                RTPI = "LUAS46";
                break;
            case "Cherrywood":
                RTPI = "LUAS47";
                break;
            case "Bride's Glen":
                RTPI = "LUAS48";
                break;
            //red
            case "The Point":
                RTPI = "LUAS57";
                break;
            case "Spencer Dock":
                RTPI = "LUAS56";
                break;
            case "Mayor Square - NCI":
                RTPI = "LUAS55";
                break;
            case "George's Dock":
                RTPI = "LUAS54";
                break;
            case "Connolly":
                RTPI = "LUAS23";
                break;
            case "Busaras":
                RTPI = "LUAS22";
                break;
            case "Abbey Street":
                RTPI = "LUAS21";
                break;
            case "Jervis":
                RTPI = "LUAS20";
                break;
            case "Four Courts":
                RTPI = "LUAS19";
                break;
            case "Smithfield":
                RTPI = "LUAS18";
                break;
            case "Museum":
                RTPI = "LUAS17";
                break;
            case "Heuston":
                RTPI = "LUAS16";
                break;
            case "James's":
                RTPI = "LUAS15";
                break;
            case "Fatima":
                RTPI = "LUAS14";
                break;
            case "Rialto":
                RTPI = "LUAS13";
                break;
            case "Suir Road":
                RTPI = "LUAS12";
                break;
            case "Goldenbridge":
                RTPI = "LUAS11";
                break;
            case "Drimnagh":
                RTPI = "LUAS10";
                break;
            case "Blackhorse":
                RTPI = "LUAS9";
                break;
            case "Bluebell":
                RTPI = "LUAS8";
                break;
            case "Kylemore":
                RTPI = "LUAS7";
                break;
            case "Red Cow":
                RTPI = "LUAS6";
                break;
            case "Kingswood":
                RTPI = "LUAS5";
                break;
            case "Belgard":
                RTPI = "LUAS4";
                break;
            case "Cookstown":
                RTPI = "LUAS3";
                break;
            case "Hospital":
                RTPI = "LUAS2";
                break;
            case "Tallaght":
                RTPI = "LUAS1";
                break;
            case "Fettercairn":
                RTPI = "LUAS49";
                break;
            case "Cheeverstown":
                RTPI = "LUAS50";
                break;
            case "Citywest Campus":
                RTPI = "LUAS51";
                break;
            case "Fortunestown":
                RTPI = "LUAS52";
                break;
            case "Saggart":
                RTPI = "LUAS53";
                break;
        }
        return RTPI;
    }

    public void setNavName(String navName) {
        this.navName = navName;
    }

    public String getNavName() {
        return navName;
    }

    public void setNavLeapNumber(String navLeapNumber) {
        this.navLeapNumber = navLeapNumber;
    }

    public String getNavLeapNumber() {
        return navLeapNumber;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setTag(String tag){this.tag=tag;}

    public String getTag(){return tag;}
}