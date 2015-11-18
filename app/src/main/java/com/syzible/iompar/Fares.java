package com.syzible.iompar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static com.syzible.iompar.Fares.FareType.ADULT;

/**
 * Created by ed on 29/10/15.
 */
public class Fares extends Fragment {

    View view;

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

    private double fare;

    Double[] faresSingleEuroAdult = {
            1.80,
            2.20,
            2.60,
            2.80,
            3.00
    };

    Double[] faresSingleEuroChild = {
            1.00,
            1.00,
            1.00,
            1.20,
            1.20
    };

    Double[] faresReturnAdult = {
            3.40,
            4.00,
            4.80,
            5.20,
            5.50
    };

    Double[] faresReturnChild = {
            1.70,
            1.70,
            1.70,
            2.10,
            2.10
    };

    Double[] luasDailyCap = {
            6.40,
            2.50,
            5.00
    };

    Double[] luasWeeklyCap = {
            23.50,
            8.20,
            18.00
    };

    Double[] dublinBusLuasDARTCommuterDailyCap = {
            10.00,
            3.50,
            7.50
    };

    Double[] dublinBusLuasDARTCommuterWeeklyCap = {
            40.00,
            14.00,
            30.00
    };

    Double[] faresLeapOffPeakAdultStudent = {
            1.39,
            1.70,
            2.03,
            2.19,
            2.35
    };

    Double[] faresLeapPeakAdultStudent = {
            1.44,
            1.75,
            2.08,
            2.24,
            2.40
    };

    Double[] faresLeapOffPeakChild = {
            0.80,
            0.80,
            0.80,
            0.96,
            0.96
    };

    Double[] faresLeapPeakChild = {
            0.80,
            0.80,
            0.80,
            0.96,
            0.96
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_fare, null);
        return view;
    }

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
                                                setFare(faresSingleEuroAdult[1]);
                                                break;
                                            case TWO_ZONES:
                                                setFare(faresSingleEuroAdult[2]);
                                                break;
                                            case THREE_ZONES:
                                                setFare(faresSingleEuroAdult[3]);
                                                break;
                                            case FOUR_ZONES:
                                                setFare(faresSingleEuroAdult[4]);
                                                break;
                                            case FIVE_EIGHT_ZONES:
                                                setFare(faresSingleEuroAdult[5]);
                                                break;
                                        }
                                        break;
                                    case LEAP:
                                        switch (getLuasFareCost()) {
                                            case ONE_ZONE:
                                                setFare(faresLeapPeakAdultStudent[1]);
                                                break;
                                            case TWO_ZONES:
                                                setFare(faresLeapPeakAdultStudent[2]);
                                                break;
                                            case THREE_ZONES:
                                                setFare(faresLeapPeakAdultStudent[3]);
                                                break;
                                            case FOUR_ZONES:
                                                setFare(faresLeapPeakAdultStudent[4]);
                                                break;
                                            case FIVE_EIGHT_ZONES:
                                                setFare(faresLeapPeakAdultStudent[5]);
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
                                                setFare(faresReturnAdult[1]);
                                                break;
                                            case TWO_ZONES:
                                                setFare(faresReturnAdult[2]);
                                                break;
                                            case THREE_ZONES:
                                                setFare(faresReturnAdult[3]);
                                                break;
                                            case FOUR_ZONES:
                                                setFare(faresReturnAdult[4]);
                                                break;
                                            case FIVE_EIGHT_ZONES:
                                                setFare(faresReturnAdult[5]);
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
                                                setFare(faresSingleEuroAdult[1]);
                                                break;
                                            case TWO_ZONES:
                                                setFare(faresSingleEuroAdult[2]);
                                                break;
                                            case THREE_ZONES:
                                                setFare(faresSingleEuroAdult[3]);
                                                break;
                                            case FOUR_ZONES:
                                                setFare(faresSingleEuroAdult[4]);
                                                break;
                                            case FIVE_EIGHT_ZONES:
                                                setFare(faresSingleEuroAdult[5]);
                                                break;
                                        }
                                        break;
                                    case LEAP:
                                        switch (getLuasFareCost()) {
                                            case ONE_ZONE:
                                                setFare(faresLeapOffPeakAdultStudent[1]);
                                                break;
                                            case TWO_ZONES:
                                                setFare(faresLeapOffPeakAdultStudent[2]);
                                                break;
                                            case THREE_ZONES:
                                                setFare(faresLeapOffPeakAdultStudent[3]);
                                                break;
                                            case FOUR_ZONES:
                                                setFare(faresLeapOffPeakAdultStudent[4]);
                                                break;
                                            case FIVE_EIGHT_ZONES:
                                                setFare(faresLeapOffPeakAdultStudent[5]);
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
                                                setFare(faresReturnAdult[1]);
                                                break;
                                            case TWO_ZONES:
                                                setFare(faresReturnAdult[2]);
                                                break;
                                            case THREE_ZONES:
                                                setFare(faresReturnAdult[3]);
                                                break;
                                            case FOUR_ZONES:
                                                setFare(faresReturnAdult[4]);
                                                break;
                                            case FIVE_EIGHT_ZONES:
                                                setFare(faresReturnAdult[5]);
                                                break;
                                        }
                                        break;
                                }
                                break;
                        }
                }
                break;
            case STUDENT:
                break;
            case CHILD:
                break;
            case OTHER:
                break;
        }
    }

    public void setFare(double fare) {
        this.fare = fare;
    }

    public double getFare() {
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
