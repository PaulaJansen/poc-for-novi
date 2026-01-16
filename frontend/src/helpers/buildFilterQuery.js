export function buildFilterQuery(filters) {
    const params = new URLSearchParams();

    if (filters.title) params.append("title", filters.title);
    if (filters.artistFirstName) params.append("artistFirstName", filters.artistFirstName);
    if (filters.artistLastName) params.append("artistLastName", filters.artistLastName);
    if (filters.minPrice) params.append("minPrice", filters.minPrice);
    if (filters.maxPrice) params.append("maxPrice", filters.maxPrice);

    filters.genres.forEach(genre => params.append("genres", genre));
    filters.availabilities.forEach(a => params.append("availabilities", a));

    return params.toString();
}