package com.example.oniongarlicrun.utils
import android.content.Context
import com.example.oniongarlicrun.HighScore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.content.edit

object ScoreManager {
    private const val KEY = "high_scores"

    fun save(context: Context, scores: List<HighScore>) {
        val json = Gson().toJson(scores)
        context.getSharedPreferences("scores", Context.MODE_PRIVATE)
            .edit { putString(KEY, json) }
    }

    fun load(context: Context): MutableList<HighScore> {
        val prefs = context.getSharedPreferences("scores", Context.MODE_PRIVATE)
        val json = prefs.getString(KEY, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<HighScore>>() {}.type
        return Gson().fromJson(json, type) ?: mutableListOf()
    }

    fun tryInsert(context: Context, newScore: HighScore) {
        val scores = load(context)
        scores.add(newScore)
        val top10 = scores.sortedByDescending { it.score }.take(10)
        save(context, top10)
    }
}