export function buildFilterQuery(filters) {
    const params = new URLSearchParams();

    if (filters.title) params.append("title", filters.title);
    if (filters.artistFirstName) params.append("artistFirstName", filters.artistFirstName);
    if (filters.artistLastName) params.append("artistLastName", filters.artistLastName);
    if (filters.genre?.trim()) {
        params.append("genres", filters.genre.trim());
    }
    if (filters.minPrice) params.append("minPrice", filters.minPrice);
    if (filters.maxPrice) params.append("maxPrice", filters.maxPrice);
    if (filters.availabilities?.length > 0) {
        filters.availabilities.forEach(a => params.append("availabilities", a));
    }

    return params.toString();
}