package com.example.paycalc

object Constants {

    object States {
        const val ALABAMA = "Alabama"
        const val ALASKA = "Alaska"
        const val ARIZONA = "Arizona"
        const val ARKANSAS = "Arkansas"
        const val CALIFORNIA = "California"
        const val COLORADO = "Colorado"
        const val CONNECTICUT = "Connecticut"
        const val DELAWARE = "Delaware"
        const val FLORIDA = "Florida"
        const val GEORGIA = "Georgia"
        const val HAWAII = "Hawaii"
        const val IDAHO = "Idaho"
        const val ILLINOIS = "Illinois"
        const val INDIANA = "Indiana"
        const val IOWA = "Iowa"
        const val KANSAS = "Kansas"
        const val KENTUCKY = "Kentucky"
        const val LOUISIANA = "Louisiana"
        const val MAINE = "Maine"
        const val MARYLAND = "Maryland"
        const val MASSACHUSETTS = "Massachusetts"
        const val MICHIGAN = "Michigan"
        const val MINNESOTA = "Minnesota"
        const val MISSISSIPPI = "Mississippi"
        const val MISSOURI = "Missouri"
        const val MONTANA = "Montana"
        const val NEBRASKA = "Nebraska"
        const val NEVADA = "Nevada"
        const val NEW_HAMPSHIRE = "New Hampshire"
        const val NEW_JERSEY = "New Jersey"
        const val NEW_MEXICO = "New Mexico"
        const val NEW_YORK = "New York"
        const val NORTH_CAROLINA = "North Carolina"
        const val NORTH_DAKOTA = "North Dakota"
        const val OHIO = "Ohio"
        const val OKLAHOMA = "Oklahoma"
        const val OREGON = "Oregon"
        const val PENNSYLVANIA = "Pennsylvania"
        const val RHODE_ISLAND = "Rhode Island"
        const val SOUTH_CAROLINA = "South Carolina"
        const val SOUTH_DAKOTA = "South Dakota"
        const val TENNESSEE = "Tennessee"
        const val TEXAS = "Texas"
        const val UTAH = "Utah"
        const val VERMONT = "Vermont"
        const val VIRGINIA = "Virginia"
        const val WASHINGTON = "Washington"
        const val WEST_VIRGINIA = "West Virginia"
        const val WISCONSIN = "Wisconsin"
        const val WYOMING = "Wyoming"
    }

    object FederalMaritalStatuses {
        const val SINGLE = "Single"
        const val MARRIED = "Married"
        const val MARRIED_SINGLE = "Married, but withhold at higher Single rate"
    }



    object Frequencies {
        const val DAILY = "Daily"
        const val WEEKLY = "Weekly"
        const val BIWEEKLY = "Biweekly"
        const val SEMIMONTHLY = "Semimonthly"
        const val MONTHLY = "Monthly"
        const val QUARTERLY = "Quarterly"
        const val SEMIANNUALLY = "Semiannually"
        const val ANNUALLY = "Annually"
    }

    object AllowanceAmounts {
        const val DAILY = 16.20f
        const val WEEKLY = 80.80f
        const val BIWEEKLY = 161.50f
        const val SEMIMONTHLY = 175f
        const val MONTHLY = 350f
        const val QUARTERLY = 10500f
        const val SEMIANNUALLY = 2100f
        const val ANNUAL = 42000f
    }

    /**
     * State Constants
     */

    // Alabama

    object AlabamaExemptions {
        const val ZERO = "0"
        const val SINGLE = "Single (S)"
        const val MARRIED_FILING_SEPARATELY = "Married Filing Separately (MS)"
        const val MARRIED_FILING_JOINTLY = "Married Filing Jointly (MJ)"
        const val HEAD_OF_FAMILY = "Head of Family (H)"
    }

    // Arizona

    object ArizonaConstantRates {
        const val ZERO_POINT_EIGHT = "0.8%"
        const val ONE_POINT_THREE = "1.3%"
        const val ONE_POINT_EIGHT = "1.8%"
        const val TWO_POINT_SEVEN = "2.7%"
        const val THREE_POINT_SIX = "3.6%"
        const val FOUR_POINT_TWO = "4.2%"
        const val FIVE_POINT_ONE = "5.1%"
    }
}