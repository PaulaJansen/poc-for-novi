export function hasGenre(artwork, genre) {
    return artwork.genreNames?.some(
        g => g.toLowerCase() === genre.toLowerCase()
    );
}