package com.syzible.iompar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static com.syzible.iompar.Fares.FareType.ADULT;

/**
 * Created by ed on 29/10/15.
 */
public class Fares extends Fragment {

    View view;
    Globals globals = new Globals();

    int index;

    public enum FareType {ADULT, STUDENT, CHILD, OTHER}

    public enum FareCaps {ON_PEAK, OFF_PEAK}

    public enum FarePayment {LEAP, CASH}

    public enum FareJourney {SINGLE, RETURN}

    public enum LuasFareCost {ONE_ZONE, TWO_ZONES, THREE_ZONES, FOUR_ZONES, FIVE_EIGHT_ZONES}

    public enum LuasZones {DOCKLANDS, CENTRAL_1, RED_2, RED_3, RED_4, GREEN_2, GREEN_3, GREEN_4, GREEN_5}

    FareType fareType;
    FareCaps fareCaps;
    FarePayment farePayment;
    FareJourney fareJourney;
    LuasFareCost luasFareCost;
    LuasZones luasZonesStart, luasZonesEnd;

    private String fare;

    String[] faresSingleEuroAdult = {
            "1.90",
            "2.30",
            "2.70",
            "2.80",
            "3.10"
    };

    String[] faresSingleEuroChild = {
            "1.00",
            "1.00",
            "1.00",
            "1.20",
            "1.20"
    };

    String[] faresReturnAdult = {
            "3.50",
            "4.10",
            "4.90",
            "5.30",
            "5.60"
    };

    String[] faresReturnChild = {
            "1.70",
            "1.70",
            "1.70",
            "2.10",
            "2.10"
    };

    String[] luasDailyCap = {
            "6.40",
            "2.50",
            "5.00"
    };

    String[] luasWeeklyCap = {
            "23.50",
            "8.20",
            "18.00"
    };

    String[] dublinBusLuasDARTCommuterDailyCap = {
            "10.00",
            "3.50",
            "7.50"
    };

    String[] dublinBusLuasDARTCommuterWeeklyCap = {
            "40.00",
            "14.00",
            "30.00"
    };

    String[] faresLeapOffPeakAdultStudent = {
            "1.39",
            "1.70",
            "2.03",
            "2.19",
            "2.35"
    };

    String[] faresLeapPeakAdultStudent = {
            "1.44",
            "1.70",
            "2.05",
            "2.19",
            "2.35"
    };

    String[] faresLeapOffPeakChild = {
            "0.80",
            "0.80",
            "0.80",
            "0.96",
            "0.96"
    };

    String[] faresLeapPeakChild = {
            "0.80",
            "0.80",
            "0.80",
            "0.96",
            "0.96"
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        globals.setTag("Fares");
        view = inflater.inflate(R.layout.fragment_fare, null);

        setFareType(FareType.STUDENT);
        setFareJourney(FareJourney.SINGLE);
        setFarePayment(FarePayment.CASH);

        if(isPeak()){
            setFareCaps(FareCaps.ON_PEAK);
            System.out.println("It is currently peak hour");
        } else {
            setFareCaps(FareCaps.OFF_PEAK);
            System.out.println("It is currently NOT peak hour");
        }

        String origin = "The Point";
        String destination = "Tallaght";
        int zoneIdOrigin = Globals.DOCKLANDS_ID;
        int zoneIdDestination = Globals.RED_4_ID;
        Fares.LuasZones zoneOrigin = returnStationZone(origin, zoneIdOrigin);
        Fares.LuasZones zoneDestination = returnStationZone(destination, zoneIdDestination);

        System.out.println(origin + " is in zone: " + zoneOrigin
                + " (which is " + isInZone(origin, zoneOrigin) + ")" + " (at index " + getIndex() + ")");
        System.out.println(destination + " is in zone: " + zoneDestination
                 + " (which is " + isInZone(destination, zoneDestination)  + ")" + " (at index " + getIndex() +")");

        setLuasFareCost(getZoneDifference(zoneIdOrigin, zoneIdDestination));
        calculateFare();

        System.out.println("Fare cost: €" + getFare());

        return view;
    }

    public int getOriginZoneId(String origin, String destination){

        return 0;
    }

    public LuasFareCost getZoneDifference(int origin, int destination){
        int difference = origin - destination;
        if(difference < 0){
            difference = difference * -1;
        }
        System.out.println("Zone difference: " + difference);

        switch (difference){
            case 0:
                return LuasFareCost.ONE_ZONE;
            case 1:
                return LuasFareCost.TWO_ZONES;
            case 2:
                return LuasFareCost.THREE_ZONES;
            case 3:
                return LuasFareCost.FOUR_ZONES;
            case 4:
            case 5:
            case 6:
            case 7:
                return LuasFareCost.FIVE_EIGHT_ZONES;
            default:
                return null;
        }
    }

    public LuasZones returnStationZone(String station, int zoneId){
        switch(zoneId){
            case 1:
                for (String searchStation : Globals.docklands) {
                    setIndex(getIndex() + 1);
                    if (searchStation.contains(station)){
                        return LuasZones.DOCKLANDS;
                    }
                }
                break;
            case 2:
                for (String searchStation : Globals.central_1) {
                    setIndex(getIndex() + 1);
                    if (searchStation.contains(station)){
                        return LuasZones.CENTRAL_1;
                    }
                }
                break;
            case 3:
                for (String searchStation : Globals.red_2) {
                    setIndex(getIndex() + 1);
                    if (searchStation.contains(station)){
                        return LuasZones.RED_2;
                    }
                }
                break;
            case 4:
                for (String searchStation : Globals.red_3) {
                    setIndex(getIndex() + 1);
                    if (searchStation.contains(station)){
                        return LuasZones.RED_3;
                    }
                }
                break;
            case 5:
                for (String searchStation : Globals.red_4) {
                    setIndex(getIndex() + 1);
                    if (searchStation.contains(station)){
                        return LuasZones.RED_4;
                    }
                }
                break;
            case 6:
                for (String searchStation : Globals.green_2) {
                    setIndex(getIndex() + 1);
                    if (searchStation.contains(station)){
                        return LuasZones.GREEN_2;
                    }
                }
                break;
            case 7:
                for (String searchStation : Globals.green_3) {
                    setIndex(getIndex() + 1);
                    if (searchStation.contains(station)){
                        return LuasZones.GREEN_3;
                    }
                }
                break;
            case 8:
                for (String searchStation : Globals.green_4) {
                    setIndex(getIndex() + 1);
                    if (searchStation.contains(station)){
                        return LuasZones.GREEN_4;
                    }
                }
                break;
            case 9:
                for (String searchStation : Globals.green_5) {
                    setIndex(getIndex() + 1);
                    if (searchStation.contains(station)){
                        return LuasZones.GREEN_5;
                    }
                }
                break;
        }
        return null;
    }

    public boolean isInZone(String startStation, LuasZones line){
        setIndex(0);
        switch (line){
            case DOCKLANDS:
                for (String station : Globals.docklands) {
                    setIndex(getIndex() + 1);
                    if (station.contains(startStation)){
                        return true;
                    }
                }
                break;
            case CENTRAL_1:
                for (String station : Globals.central_1) {
                    setIndex(getIndex() + 1);
                    if (station.contains(startStation)){
                        return true;
                    }
                }
                break;
            case RED_2:
                for (String station : Globals.red_2) {
                    setIndex(getIndex() + 1);
                    if (station.contains(startStation)){
                        return true;
                    }
                }
                break;
            case RED_3:
                for (String station : Globals.red_3) {
                    setIndex(getIndex() + 1);
                    if (station.contains(startStation)){
                        return true;
                    }
                }
                break;
            case RED_4:
                for (String station : Globals.red_4) {
                    setIndex(getIndex() + 1);
                    if (station.contains(startStation)){
                        return true;
                    }
                }
                break;
        }
        return false;
    }

    public void setIndex(int index){this.index=index;}
    public int getIndex(){return index;}

    public void calculateFare() {
        switch (getFareType()) {
            case ADULT:
                switch (getFareCaps()) {
                    case ON_PEAK:
                        switch (getFareJourney()) {
                            case SINGLE:
                                switch (getFarePayment()) {
                                    case CASH:
                                        switch (getLuasFareCost()) {
                                            case ONE_ZONE:
                                                setFare(faresSingleEuroAdult[0]);
                                                break;
                                            case TWO_ZONES:
                                                setFare(faresSingleEuroAdult[1]);
                                                break;
                                            case THREE_ZONES:
                                                setFare(faresSingleEuroAdult[2]);
                                                break;
                                            case FOUR_ZONES:
                                                setFare(faresSingleEuroAdult[3]);
                                                break;
                                            case FIVE_EIGHT_ZONES:
                                                setFare(faresSingleEuroAdult[4]);
                                                break;
                                        }
                                        break;
                                    case LEAP:
                                        switch (getLuasFareCost()) {
                                            case ONE_ZONE:
                                                setFare(faresLeapPeakAdultStudent[0]);
                                                break;
                                            case TWO_ZONES:
                                                setFare(faresLeapPeakAdultStudent[1]);
                                                break;
                                            case THREE_ZONES:
                                                setFare(faresLeapPeakAdultStudent[2]);
                                                break;
                                            case FOUR_ZONES:
                                                setFare(faresLeapPeakAdultStudent[3]);
                                                break;
                                            case FIVE_EIGHT_ZONES:
                                                setFare(faresLeapPeakAdultStudent[4]);
                                                break;
                                        }
                                        break;
                                }
                                break;
                            case RETURN:
                                switch (getFarePayment()) {
                                    case CASH:
                                        switch (getLuasFareCost()) {
                                            case ONE_ZONE:
                                                setFare(faresReturnAdult[0]);
                                                break;
                                            case TWO_ZONES:
                                                setFare(faresReturnAdult[1]);
                                                break;
                                            case THREE_ZONES:
                                                setFare(faresReturnAdult[2]);
                                                break;
                                            case FOUR_ZONES:
                                                setFare(faresReturnAdult[3]);
                                                break;
                                            case FIVE_EIGHT_ZONES:
                                                setFare(faresReturnAdult[4]);
                                                break;
                                        }
                                        break;
                                }
                                break;
                        }
                    case OFF_PEAK:
                        switch (getFareJourney()) {
                            case SINGLE:
                                switch (getFarePayment()) {
                                    case CASH:
                                        switch (getLuasFareCost()) {
                                            case ONE_ZONE:
                                                setFare(faresSingleEuroAdult[0]);
                                                break;
                                            case TWO_ZONES:
                                                setFare(faresSingleEuroAdult[1]);
                                                break;
                                            case THREE_ZONES:
                                                setFare(faresSingleEuroAdult[2]);
                                                break;
                                            case FOUR_ZONES:
                                                setFare(faresSingleEuroAdult[3]);
                                                break;
                                            case FIVE_EIGHT_ZONES:
                                                setFare(faresSingleEuroAdult[4]);
                                                break;
                                        }
                                        break;
                                    case LEAP:
                                        switch (getLuasFareCost()) {
                                            case ONE_ZONE:
                                                setFare(faresLeapOffPeakAdultStudent[0]);
                                                break;
                                            case TWO_ZONES:
                                                setFare(faresLeapOffPeakAdultStudent[1]);
                                                break;
                                            case THREE_ZONES:
                                                setFare(faresLeapOffPeakAdultStudent[2]);
                                                break;
                                            case FOUR_ZONES:
                                                setFare(faresLeapOffPeakAdultStudent[3]);
                                                break;
                                            case FIVE_EIGHT_ZONES:
                                                setFare(faresLeapOffPeakAdultStudent[4]);
                                                break;
                                        }
                                        break;
                                }
                                break;
                            case RETURN:
                                switch (getFarePayment()) {
                                    case CASH:
                                        switch (getLuasFareCost()) {
                                            case ONE_ZONE:
                                                setFare(faresReturnAdult[0]);
                                                break;
                                            case TWO_ZONES:
                                                setFare(faresReturnAdult[1]);
                                                break;
                                            case THREE_ZONES:
                                                setFare(faresReturnAdult[2]);
                                                break;
                                            case FOUR_ZONES:
                                                setFare(faresReturnAdult[3]);
                                                break;
                                            case FIVE_EIGHT_ZONES:
                                                setFare(faresReturnAdult[4]);
                                                break;
                                        }
                                        break;
                                }
                                break;
                        }
                }
                break;
            case STUDENT:
                switch (getFareCaps()) {
                    case ON_PEAK:
                        switch (getFareJourney()) {
                            case SINGLE:
                                switch (getFarePayment()) {
                                    case CASH:
                                        switch (getLuasFareCost()) {
                                            case ONE_ZONE:
                                                setFare(faresSingleEuroAdult[0]);
                                                break;
                                            case TWO_ZONES:
                                                setFare(faresSingleEuroAdult[1]);
                                                break;
                                            case THREE_ZONES:
                                                setFare(faresSingleEuroAdult[2]);
                                                break;
                                            case FOUR_ZONES:
                                                setFare(faresSingleEuroAdult[3]);
                                                break;
                                            case FIVE_EIGHT_ZONES:
                                                setFare(faresSingleEuroAdult[4]);
                                                break;
                                        }
                                        break;
                                    case LEAP:
                                        switch (getLuasFareCost()) {
                                            case ONE_ZONE:
                                                setFare(faresLeapPeakAdultStudent[0]);
                                                break;
                                            case TWO_ZONES:
                                                setFare(faresLeapPeakAdultStudent[1]);
                                                break;
                                            case THREE_ZONES:
                                                setFare(faresLeapPeakAdultStudent[2]);
                                                break;
                                            case FOUR_ZONES:
                                                setFare(faresLeapPeakAdultStudent[3]);
                                                break;
                                            case FIVE_EIGHT_ZONES:
                                                setFare(faresLeapPeakAdultStudent[4]);
                                                break;
                                        }
                                        break;
                                }
                                break;
                            case RETURN:
                                switch (getFarePayment()) {
                                    case CASH:
                                        switch (getLuasFareCost()) {
                                            case ONE_ZONE:
                                                setFare(faresReturnAdult[0]);
                                                break;
                                            case TWO_ZONES:
                                                setFare(faresReturnAdult[1]);
                                                break;
                                            case THREE_ZONES:
                                                setFare(faresReturnAdult[2]);
                                                break;
                                            case FOUR_ZONES:
                                                setFare(faresReturnAdult[3]);
                                                break;
                                            case FIVE_EIGHT_ZONES:
                                                setFare(faresReturnAdult[4]);
                                                break;
                                        }
                                        break;
                                }
                                break;
                        }
                    case OFF_PEAK:
                        switch (getFareJourney()) {
                            case SINGLE:
                                switch (getFarePayment()) {
                                    case CASH:
                                        switch (getLuasFareCost()) {
                                            case ONE_ZONE:
                                                setFare(faresSingleEuroAdult[0]);
                                                break;
                                            case TWO_ZONES:
                                                setFare(faresSingleEuroAdult[1]);
                                                break;
                                            case THREE_ZONES:
                                                setFare(faresSingleEuroAdult[2]);
                                                break;
                                            case FOUR_ZONES:
                                                setFare(faresSingleEuroAdult[3]);
                                                break;
                                            case FIVE_EIGHT_ZONES:
                                                setFare(faresSingleEuroAdult[4]);
                                                break;
                                        }
                                        break;
                                    case LEAP:
                                        switch (getLuasFareCost()) {
                                            case ONE_ZONE:
                                                setFare(faresLeapOffPeakAdultStudent[0]);
                                                break;
                                            case TWO_ZONES:
                                                setFare(faresLeapOffPeakAdultStudent[1]);
                                                break;
                                            case THREE_ZONES:
                                                setFare(faresLeapOffPeakAdultStudent[2]);
                                                break;
                                            case FOUR_ZONES:
                                                setFare(faresLeapOffPeakAdultStudent[3]);
                                                break;
                                            case FIVE_EIGHT_ZONES:
                                                setFare(faresLeapOffPeakAdultStudent[4]);
                                                break;
                                        }
                                        break;
                                }
                                break;
                            case RETURN:
                                switch (getFarePayment()) {
                                    case CASH:
                                        switch (getLuasFareCost()) {
                                            case ONE_ZONE:
                                                setFare(faresReturnAdult[0]);
                                                break;
                                            case TWO_ZONES:
                                                setFare(faresReturnAdult[1]);
                                                break;
                                            case THREE_ZONES:
                                                setFare(faresReturnAdult[2]);
                                                break;
                                            case FOUR_ZONES:
                                                setFare(faresReturnAdult[3]);
                                                break;
                                            case FIVE_EIGHT_ZONES:
                                                setFare(faresReturnAdult[4]);
                                                break;
                                        }
                                        break;
                                }
                                break;
                        }
                }
                break;
            case CHILD:
                switch (getFareCaps()) {
                    case ON_PEAK:
                        switch (getFareJourney()) {
                            case SINGLE:
                                switch (getFarePayment()) {
                                    case CASH:
                                        switch (getLuasFareCost()) {
                                            case ONE_ZONE:
                                                setFare(faresSingleEuroChild[0]);
                                                break;
                                            case TWO_ZONES:
                                                setFare(faresSingleEuroChild[1]);
                                                break;
                                            case THREE_ZONES:
                                                setFare(faresSingleEuroChild[2]);
                                                break;
                                            case FOUR_ZONES:
                                                setFare(faresSingleEuroChild[3]);
                                                break;
                                            case FIVE_EIGHT_ZONES:
                                                setFare(faresSingleEuroChild[4]);
                                                break;
                                        }
                                        break;
                                    case LEAP:
                                        switch (getLuasFareCost()) {
                                            case ONE_ZONE:
                                                setFare(faresLeapPeakChild[0]);
                                                break;
                                            case TWO_ZONES:
                                                setFare(faresLeapPeakChild[1]);
                                                break;
                                            case THREE_ZONES:
                                                setFare(faresLeapPeakChild[2]);
                                                break;
                                            case FOUR_ZONES:
                                                setFare(faresLeapPeakChild[3]);
                                                break;
                                            case FIVE_EIGHT_ZONES:
                                                setFare(faresLeapPeakChild[4]);
                                                break;
                                        }
                                        break;
                                }
                                break;
                            case RETURN:
                                switch (getFarePayment()) {
                                    case CASH:
                                        switch (getLuasFareCost()) {
                                            case ONE_ZONE:
                                                setFare(faresReturnChild[0]);
                                                break;
                                            case TWO_ZONES:
                                                setFare(faresReturnChild[1]);
                                                break;
                                            case THREE_ZONES:
                                                setFare(faresReturnChild[2]);
                                                break;
                                            case FOUR_ZONES:
                                                setFare(faresReturnChild[3]);
                                                break;
                                            case FIVE_EIGHT_ZONES:
                                                setFare(faresReturnChild[4]);
                                                break;
                                        }
                                        break;
                                }
                                break;
                        }
                    case OFF_PEAK:
                        switch (getFareJourney()) {
                            case SINGLE:
                                switch (getFarePayment()) {
                                    case CASH:
                                        switch (getLuasFareCost()) {
                                            case ONE_ZONE:
                                                setFare(faresSingleEuroChild[0]);
                                                break;
                                            case TWO_ZONES:
                                                setFare(faresSingleEuroChild[1]);
                                                break;
                                            case THREE_ZONES:
                                                setFare(faresSingleEuroChild[2]);
                                                break;
                                            case FOUR_ZONES:
                                                setFare(faresSingleEuroChild[3]);
                                                break;
                                            case FIVE_EIGHT_ZONES:
                                                setFare(faresSingleEuroChild[4]);
                                                break;
                                        }
                                        break;
                                    case LEAP:
                                        switch (getLuasFareCost()) {
                                            case ONE_ZONE:
                                                setFare(faresLeapOffPeakChild[0]);
                                                break;
                                    case TWO_ZONES:
                                                setFare(faresLeapOffPeakChild[1]);
                                                break;
                                            case THREE_ZONES:
                                                setFare(faresLeapOffPeakChild[2]);
                                                break;
                                            case FOUR_ZONES:
                                                setFare(faresLeapOffPeakChild[3]);
                                                break;
                                            case FIVE_EIGHT_ZONES:
                                                setFare(faresLeapOffPeakChild[4]);
                                                break;
                                        }
                                        break;
                                }
                                break;
                            case RETURN:
                                switch (getFarePayment()) {
                                    case CASH:
                                        switch (getLuasFareCost()) {
                                            case ONE_ZONE:
                                                setFare(faresReturnChild[0]);
                                                break;
                                            case TWO_ZONES:
                                                setFare(faresReturnChild[1]);
                                                break;
                                            case THREE_ZONES:
                                                setFare(faresReturnChild[2]);
                                                break;
                                            case FOUR_ZONES:
                                                setFare(faresReturnChild[3]);
                                                break;
                                            case FIVE_EIGHT_ZONES:
                                                setFare(faresReturnChild[4]);
                                                break;
                                        }
                                        break;
                                }
                                break;
                        }
                }
                break;
            case OTHER:
                break;
        }
    }

    public boolean isPeak(){
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        Date currentLocalTime = cal.getTime();

        DateFormat date = new SimpleDateFormat("HH:mm");
        date.setTimeZone(TimeZone.getDefault());

        String localTime = date.format(currentLocalTime);
        System.out.println(localTime);

        int day = cal.get(Calendar.DAY_OF_WEEK);
        //xx:00
        String hour = localTime.substring(0, Math.min(localTime.length(), 2));
        //00:xx
        String minutes = localTime.substring(3, Math.min(localTime.length(), 5));
        int currentHour = Integer.parseInt(hour);
        System.out.println(currentHour);
        int currentMinutes = Integer.parseInt(minutes);
        System.out.println(currentMinutes);

        //monday through friday
        //7.45am to 9.30am peak
        //else off-peak
        if(day >= 2 && day <= 6){
            if(currentHour == 7){
                if (currentMinutes >= 45){
                    return true;
                }
            } else if(currentHour == 8){
                return true;
            } else if(currentHour == 9){
                if (currentMinutes <= 30){
                    return true;
                }
            }
        }
        return false;
    }

    public String formatDecimals(double fare){
        return String.format("%.2f", fare );
    }

    public LuasFareCost getNumberOfZones(){
        return LuasFareCost.THREE_ZONES;
    }

    public void setFare(String fare) {
        this.fare = fare;
    }

    public String getFare() {
        return fare;
    }

    public void setLuasFareCost(LuasFareCost luasFareCost) {
        this.luasFareCost = luasFareCost;
    }

    public LuasFareCost getLuasFareCost() {
        return luasFareCost;
    }

    public void setFareType(FareType fareType) {
        this.fareType = fareType;
    }

    public FareType getFareType() {
        return fareType;
    }

    public void setLuasZonesStart(LuasZones luasZonesStart) {
        this.luasZonesStart = luasZonesStart;
    }

    public LuasZones getLuasZonesStart() {
        return luasZonesStart;
    }

    public void setLuasZonesEnd(LuasZones luasZonesEnd) {
        this.luasZonesEnd = luasZonesEnd;
    }

    public LuasZones setLuasZonesEnd() {
        return luasZonesEnd;
    }

    public void setFareCaps(FareCaps fareCaps) {
        this.fareCaps = fareCaps;
    }

    public FareCaps getFareCaps() {
        return fareCaps;
    }

    public void setFarePayment(FarePayment farePayment) {
        this.farePayment = farePayment;
    }

    public FarePayment getFarePayment() {
        return farePayment;
    }

    public void setFareJourney(FareJourney fareJourney) {
        this.fareJourney = fareJourney;
    }

    public FareJourney getFareJourney() {
        return fareJourney;
    }

}
