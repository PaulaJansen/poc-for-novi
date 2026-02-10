import {useState} from "react";
import {v4 as uuidv4} from "uuid";

export default function useImageUpload({maxImages = 8}) {

    const [images, setImages] = useState([]);
    const [dragIndex, setDragIndex] = useState(null);

    function setInitialImages(items) {
        setImages(
            items.map((item, index) => {
                if (typeof item === "string") {
                    return {
                        id: `db-${index}`,
                        file: null,
                        url: item.startsWith("http") ? item : `http://localhost:8080/uploads/${item}`,
                        removed: false,
                        dbPath: item
                    };
                } else {
                    return {
                        id: item.id || `db-${index}`,
                        file: item.file || null,
                        url:
                            item.url?.startsWith("http") ||
                            item.dbPath?.startsWith("https")
                                ? item.url || item.dbPath
                                : `http://localhost:8080/uploads/${item.url || item.dbPath}`,
                        removed: item.removed || false,
                        dbPath: item.dbPath || item.url,
                    };
                }
            })
        );
    }

    function addImages(files) {
        const newImages = Array.from(files)
            .filter(f => f.type.startsWith("image/"))
            .map(file => ({
                id: uuidv4(),
                file,
                url: null,
                removed: false,
                dbPath: null,
            }));

        setImages(prev => [...prev, ...newImages].slice(0, maxImages));
    }

    function removeImage(id) {
        setImages((prev) =>
            prev.map((img) => img.id === id ? {...img, removed: true} : img)
        );
    }

    function handleDragStart(id) {
        setDragIndex(id);
    }

    function handleDragEnter(overId) {
        if (dragIndex === null || dragIndex === overId) return;
        setImages(prev => {
            const from = prev.findIndex(img => img.id === dragIndex);
            const to = prev.findIndex(img => img.id === overId);

            if (from === -1 || to === -1) return prev;

            const copy = [...prev];
            const [moved] = copy.splice(from, 1);
            copy.splice(to, 0, moved);
            return copy;
        });

        setDragIndex(overId);
    }

    function handleDragEnd() {
        setDragIndex(null);
    }

    function handleFileInput(files) {
        addImages(files);
    }

    function handleDrop(e) {
        e.preventDefault();
        handleFileInput(e.dataTransfer.files);
    }

    function handleDragOver(e) {
        e.preventDefault();
    }

    return {
        images: images.filter((img) => !img.removed),
        rawImages: images,
        setInitialImages,
        handleFileInput,
        handleDrop,
        handleDragOver,
        removeImage,
        handleDragStart,
        handleDragEnter,
        handleDragEnd
    };
}