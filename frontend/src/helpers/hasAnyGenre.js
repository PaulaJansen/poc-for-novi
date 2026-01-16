export function hasAnyGenre(artwork, genres = []) {

    if (!artwork?.genreNames || genres.length === 0) return false;

    return artwork.genreNames.some(g =>
        genres.some(target =>
            g.toLowerCase() === target.toLowerCase()
        )
    );
}