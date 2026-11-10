package com.example.b2capp.data.local.search

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SearchHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearch(item: SearchHistoryEntity)

    @Query("SELECT query FROM search_history ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentSearches(limit: Int = 10): List<String>

    @Query("""
        SELECT query FROM search_history 
        WHERE query LIKE '%' || :query || '%' 
           OR normalizedQuery LIKE '%' || :normalized || '%'
        ORDER BY timestamp DESC 
        LIMIT :limit
    """)
    suspend fun getSuggestions(query: String, normalized: String, limit: Int = 8): List<String>

    @Query("DELETE FROM search_history WHERE query = :query")
    suspend fun deleteSearch(query: String)

    @Query("DELETE FROM search_history")
    suspend fun clearHistory()
}
