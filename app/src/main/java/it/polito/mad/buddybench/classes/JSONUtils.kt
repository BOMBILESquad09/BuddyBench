package it.polito.mad.buddybench.classes

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class JSONUtils {
    companion object{
        fun JSONObject.getString(key: String, defaultValue: String): String{
            return try{
                this.getString(key)
            } catch (_:JSONException){
                defaultValue
            }
        }

        fun JSONObject.getInt(key: String, defaultValue: Int): Int{
            return try{
                this.getInt(key)
            } catch (_:JSONException){
                defaultValue
            }
        }

        fun JSONObject.getBoolean(key: String, defaultValue: Boolean): Boolean {
            return try{
                this.getBoolean(key)
            } catch (_:JSONException){
                defaultValue
            }
        }

        fun JSONObject.getJSONArray(key: String, defaultValue: JSONArray): JSONArray{
            return try{
                this.getJSONArray(key)
            } catch (_: JSONException){
                defaultValue
            }
        }
    }
}