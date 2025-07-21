# Geolocation-Based Queries in Elasticsearch for Hotel Booking Websites

Elasticsearch (built on Lucene) provides powerful capabilities for performing geolocation-based queries, which are essential for applications like hotel booking websites. This document outlines common geo-query types and their usage.

## Core Concept: `geo_point` Data Type

To perform geo-queries, your hotel documents in Elasticsearch must have a field mapped as `geo_point`. This field stores the latitude and longitude of each hotel.

**Example Mapping:**
```json
PUT /hotels
{
  "mappings": {
    "properties": {
      "name": { "type": "text" },
      "location": { "type": "geo_point" }, // Stores lat/lon
      "city": { "type": "keyword" },
      "price_per_night": { "type": "integer" }
      // ... other fields
    }
  }
}
```

**Example Hotel Document:**
```json
POST /hotels/_doc/1
{
  "name": "Grand Hyatt Paris",
  "location": { "lat": 48.8738, "lon": 2.2950 }, // Can also be "48.8738,2.2950" or [2.2950, 48.8738] (lon, lat order for array)
  "city": "Paris",
  "price_per_night": 350
}
```

---

## Types of Geo Location Based Queries:

1.  **Find Hotels within a Radius (Geo Distance Query)**
    *   **Use Case:** "Show me hotels within 5 kilometers of the Eiffel Tower," or "Find hotels near my current location."
    *   **Elasticsearch Query (`geo_distance`):**
        ```json
        GET /hotels/_search
        {
          "query": {
            "bool": {
              "must": {
                "match_all": {} // Or other filters like city, price
              },
              "filter": {
                "geo_distance": {
                  "distance": "5km",
                  "location": { // The field mapped as geo_point
                    "lat": 48.8584, // Center point latitude
                    "lon": 2.2945  // Center point longitude
                  }
                }
              }
            }
          }
        }
        ```

2.  **Find Hotels within a Rectangular Area (Geo Bounding Box Query)**
    *   **Use Case:** "Show me hotels within the area currently visible on this map." This is very common for map-based searches where the client sends the coordinates of the map's viewport.
    *   **Elasticsearch Query (`geo_bounding_box`):**
        ```json
        GET /hotels/_search
        {
          "query": {
            "bool": {
              "must": {
                "match_all": {}
              },
              "filter": {
                "geo_bounding_box": {
                  "location": { // The field mapped as geo_point
                    "top_left": {
                      "lat": 40.73,
                      "lon": -74.1
                    },
                    "bottom_right": {
                      "lat": 40.01,
                      "lon": -71.12
                    }
                  }
                }
              }
            }
          }
        }
        ```

3.  **Find Hotels within a Custom Polygon (Geo Polygon Query)**
    *   **Use Case:** "Show me hotels within this specific neighborhood outline," where the neighborhood is defined by a series of connected points forming a polygon.
    *   **Elasticsearch Query (`geo_polygon`):**
        ```json
        GET /hotels/_search
        {
          "query": {
            "bool": {
              "must": {
                "match_all": {}
              },
              "filter": {
                "geo_polygon": {
                  "location": { // The field mapped as geo_point
                    "points": [
                      { "lat": 40.73, "lon": -74.1 },
                      { "lat": 40.01, "lon": -74.0 },
                      { "lat": 40.01, "lon": -71.12 },
                      { "lat": 40.73, "lon": -71.00 },
                      { "lat": 40.73, "lon": -74.1 } // Polygon should be closed (first and last points same)
                    ]
                  }
                }
              }
            }
          }
        }
        ```

4.  **Find Hotels within Predefined Shapes (Geo Shape Query)**
    *   **Use Case:** "Show me hotels in California," where "California" is a complex polygon (a shape) that you've indexed beforehand. This is useful for administrative boundaries (cities, states, countries, postal codes).
    *   **Prerequisite:** Index documents containing these shapes (e.g., in a separate `shapes_index`) using the `geo_shape` data type for a field (e.g., `geometry`).
    *   **Elasticsearch Query (`geo_shape`):**
        ```json
        GET /hotels/_search
        {
          "query": {
            "bool": {
              "filter": {
                "geo_shape": {
                  "location": { // The geo_point field in hotels index
                    "indexed_shape": { // Referencing a shape in another index
                      "index": "shapes_index",
                      "id": "california_state_boundary_id", // ID of the document containing the shape
                      "path": "geometry" // Field in shapes_index that holds the geo_shape
                    },
                    "relation": "within" // Other relations: intersects, disjoint, contains
                  }
                }
              }
            }
          }
        }
        ```

5.  **Sorting Results by Distance**
    *   **Use Case:** After finding hotels (e.g., in a city), sort them by how close they are to a specific point of interest or the user's current location.
    *   **Elasticsearch Query (`sort` by `_geo_distance`):**
        ```json
        GET /hotels/_search
        {
          "query": {
            "bool": {
              "must": { "term": { "city": "Paris" } }, // Initial filter
              "filter": { // Optional: further restrict to a broader area first for performance
                "geo_distance": {
                  "distance": "50km", // Only consider sorting for hotels within 50km
                  "location": { "lat": 48.8584, "lon": 2.2945 }
                }
              }
            }
          },
          "sort": [
            {
              "_geo_distance": {
                "location": { // The geo_point field
                  "lat": 48.8584, // Point to sort distance from
                  "lon": 2.2945
                },
                "order": "asc", // or "desc"
                "unit": "km",
                "mode": "min", // How to handle multi-valued geo_points
                "distance_type": "arc" // 'arc' (default) is more accurate, 'plane' is faster but less accurate over long distances
              }
            }
          ]
        }
        ```

---

## Geo Aggregations for Hotel Search:

Aggregations can provide summarized geo-information, useful for UI elements like map cluster counts or faceted search.

1.  **Grouping by Distance Bands (`geo_distance` aggregation)**
    *   **Use Case:** Show counts of hotels in different distance rings from a central point (e.g., "10 hotels <1km, 25 hotels 1-3km, 50 hotels 3-5km").
    *   **Elasticsearch Query:**
        ```json
        GET /hotels/_search
        {
          "query": { "term": { "city": "Paris" } },
          "aggs": {
            "hotels_by_distance_bands": {
              "geo_distance": {
                "field": "location",
                "origin": { "lat": 48.8584, "lon": 2.2945 },
                "unit": "km",
                "ranges": [
                  { "to": 1 },
                  { "from": 1, "to": 3 },
                  { "from": 3, "to": 5 },
                  { "from": 5 }
                ]
              }
            }
          },
          "size": 0 // We only want aggregation results, not the hits
        }
        ```

2.  **Clustering for Map Views (`geohash_grid` or `geo_tile` aggregation)**
    *   **Use Case:** When zoomed out on a map, show clusters of hotels (e.g., a circle with "150+" over an area) instead of thousands of individual pins. `geohash_grid` divides the map into cells based on geohashes. `geo_tile` uses map tiles (often aligned with web mapping tile systems).
    *   **Elasticsearch Query (`geohash_grid`):**
        ```json
        GET /hotels/_search
        {
          "query": {
            "geo_bounding_box": { // Filter by current map view
              "location": {
                "top_left": { "lat": 40.73, "lon": -74.1 },
                "bottom_right": { "lat": 40.01, "lon": -71.12 }
              }
            }
          },
          "aggs": {
            "hotel_clusters": {
              "geohash_grid": {
                "field": "location",
                "precision": 5 // Adjust precision based on zoom level (1-12); higher precision = smaller cells
              },
              "aggs": { // Optional: sub-aggregation to get the centroid of each cluster for placing the marker
                "centroid": {
                  "geo_centroid": { "field": "location" }
                }
              }
            }
          },
          "size": 0
        }
        ```

---

## Combining Geo Queries with Other Filters:

All these geo queries are typically used within the `filter` context of a `bool` query in Elasticsearch for performance, as filters are cacheable and don't affect scoring. They can be combined with other filters like price range, amenities, star rating, etc.

**Example:** Hotels within 5km, costing less than $200, with "wifi".
```json
GET /hotels/_search
{
  "query": {
    "bool": {
      "must": [
        { "term": { "amenities": "wifi" } },
        { "range": { "price_per_night": { "lte": 200 } } }
      ],
      "filter": {
        "geo_distance": {
          "distance": "5km",
          "location": { "lat": 48.8584, "lon": 2.2945 }
        }
      }
    }
  }
}
```

---

## Performance Considerations:

*   **Filter Context:** Use geo-queries within the `filter` context of a `bool` query whenever possible. Filters are cacheable and generally faster as they don't contribute to the relevance score.
*   **`geo_shape` Complexity:** For `geo_shape` queries with complex indexed shapes, ensure the shapes are valid and not overly complex if query performance is critical. Simpler shapes query faster.
*   **Aggregation Precision:** Adjust `precision` for `geohash_grid` or `geo_tile` aggregations based on map zoom levels. Higher precision means more buckets and potentially slower aggregation but finer-grained clusters.
*   **Hardware:** Ensure appropriate hardware (SSDs, sufficient RAM) for Elasticsearch nodes handling heavy geo-workloads.
*   **Pre-filtering:** For expensive geo-queries (like sorting a very large dataset by distance), consider applying a broader, cheaper filter first (e.g., filter by city or a larger bounding box) to reduce the number of documents the geo-sort or precise geo-filter needs to operate on.
