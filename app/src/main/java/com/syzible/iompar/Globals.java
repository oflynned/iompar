package com.syzible.iompar;

import android.widget.ImageView;

/**
 * Created by ed on 28/10/15.
 */
public class Globals {
    //profile
    String navName = "";
    String navLeapNumber = "";
    ImageView navProfilePicture;
    boolean loggedIn;

    //http querying
    public String RTPI = "http://www.rtpi.ie/Text/WebDisplay.aspx?stopRef=";
    public static String LEAP_LOGIN = "https://www.leapcard.ie/en/Login.aspx";

    public static final String USER_NAME = "oflynned";
    public static final String USER_PASS = "thuga8Da!";

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

    public String redLineBeforeBelgard[] = {

    };

    public String redLineBeforeTallaght[] = {

    };

    public String redLineBeforeSaggart[] = {

    };

    public String redLineTallaghtToPoint[] = {

    };

    public String redLineSaggardToPoint[] = {

    };



    public enum Type {
        dublin_bus,
        bus_eireann,
        luas,
        dart,
        commuter_rail,
        regional_rail
    }

    public enum Line {
        red,
        green
    }

    public enum LineDirection {
        the_point_to_tallaght,
        the_point_to_saggart,
        saggart_to_the_point,
        tallaght_to_the_point,
        stephens_green_to_brides_glen,
        stephens_green_to_sandyford,
        sandyford_to_stephens_green,
        brides_glen_to_stephens_green
    }

    public String getLuasStation(String string) {
        String RTPI = "";
        switch(string){
            case "St. Stephen's Green":
                RTPI = "LUAS24";
                break;
            case "The Gallops":
                RTPI = "LUAS39";
                break;
        }
        return RTPI;
    }

    public String getLuasLine(Line line){
        String luasLine = "";
        switch(line){
            case green:
                luasLine = "Green";
                break;
            case red:
                luasLine = "Red";
                break;
            default:
                break;
        }
        return luasLine;
    }

    public String getLuasDirection(LineDirection lineDirection) {
        String direction = "";
        switch(lineDirection){
            case stephens_green_to_sandyford:
                direction = "Sandyford";
                break;
            case stephens_green_to_brides_glen:
                direction = "Bride's Glen";
                break;
            case sandyford_to_stephens_green:
                direction = "St. Stephen's Green";
                break;
            case brides_glen_to_stephens_green:
                direction = "St. Stephen's Green";
                break;
            default:
                break;
        }
        return direction;
    }

    public void setNavName(String navName){
        this.navName = navName;
    }

    public String getNavName(){
        return navName;
    }

    public void setNavLeapNumber(String navLeapNumber){
        this.navLeapNumber = navLeapNumber;
    }

    public String getNavLeapNumber(){
        return navLeapNumber;
    }

    public void setLoggedIn(boolean loggedIn){
        this.loggedIn = loggedIn;
    }

    public boolean isLoggedIn(){
        return loggedIn;
    }
}
