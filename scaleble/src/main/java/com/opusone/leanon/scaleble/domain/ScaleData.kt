package com.opusone.leanon.scaleble.domain

import com.yolanda.health.qnblesdk.out.QNScaleItemData

class ScaleData(
    var weight: String? = null,              //몸부게
    var bmi: String? = null,                 //bmi
    var bodyFatRate: String? = null,         //체지방률
    var subcutaneousFat: String? = null,     //피하지방
    var visceralFat: String? = null,         //내장지방
    var bodyWaterRate: String? = null,       //체수분
    var muscleRate: String? = null,          //근육률
    var boneMass: String? = null,            //뼈무게
    var bmr: String? = null,                 //기초대사량
    var protein: String? = null,             //단백질
    var lbm: String? = null,                 //무지방 체중
    var mucsleMass: String? = null,          //근육량
    var bodyAge: String? = null,             //신체나이
    var bodyShape: String? = null,           //체형
    var heartRate: String? = null          //심박수
) {

    companion object {

        fun fromWeight(data : Double) : ScaleData = ScaleData().apply { weight = data.toString() }
        fun from(data: List<QNScaleItemData>): ScaleData =
            ScaleData().apply {
                if (data.size >= 1) {
                    weight = data[0].value.toString()
                }

                if (data.size >= 2) {
                    bmi = data[1].value.toString()
                }

                if (data.size >= 3) {
                    bodyFatRate = data[2].value.toString()
                }

                if (data.size >= 4) {
                    subcutaneousFat = data[3].value.toString()
                }

                if (data.size >= 5) {
                    visceralFat = data[4].value.toString()
                }

                if (data.size >= 6) {
                    bodyWaterRate = data[5].value.toString()
                }

                if (data.size >= 7) {
                    muscleRate = if (data[0].value.toInt() < data[6].value.toInt()) {
                        "0.0"
                    } else {
                        data[6].value.toString()
                    }
                }

                if (data.size >= 8) {
                    boneMass = data[7].value.toString()
                }

                if (data.size >= 9) {
                    bmr = data[8].value.toString()
                }

                if (data.size >= 11) {
                    protein = data[10].value.toString()
                }

                if (data.size >= 13) {
                    mucsleMass = data[12].value.toString()
                }

                if (data.size >= 14) {
                    bodyAge = data[13].value.toString()
                }

                if (data.size >= 16) {
                    heartRate = data[15].value.toString()
                }
            }
    }
}