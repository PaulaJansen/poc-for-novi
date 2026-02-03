import {useEffect, useState} from "react";
import {v4 as uuidv4} from "uuid";

export default function useImageUpload({setValue, maxImages = 8, initialImages = []}) {
    const [images, setImagesState] = useState(
        initialImages.map(img => ({
            id: img.id || uuidv4(),
            file: img.file || null,
            url: img.url || null
        }))
    )

    const [dragIndex, setDragIndex] = useState(null);

    useEffect(() => {
        return () => {
            images.forEach(img => {
                if (img.file) URL.revokeObjectURL(img.file);
            });
        };
    }, [images]);

    function updateImages(updater) {
        if (typeof updater === "function") {
            setImagesState(prev => {
                const updated = updater([...prev]);
                setValue("images", updated);
                return updated;
            });
        }
    }

    function setImages(newImages) {
        setImagesState(newImages);
        setValue("images", newImages);
    }

    function addImages(files) {
        const newImages = files.map(f => ({
            id: uuidv4(),
            file: f,
            url: null
        }));
        updateImages(prev => [...prev, ...newImages].slice(0, maxImages));
    }

    function removeImage(index) {
        updateImages(prev => prev.filter((_, i) => i !== index));
    }

    function moveImages(from, to) {
        if (from === to) return;

        updateImages(prev => {
            const item = prev[from];
            prev.splice(from, 1);
            prev.splice(to, 0, item);
            return prev;
        });
    }

    function handleDragStart(index) {
        setDragIndex(index);
    }

    function handleDragEnter(targetIndex) {
        if (dragIndex === null || dragIndex === targetIndex) return;
        moveImages(dragIndex, targetIndex);
        setDragIndex(targetIndex);
    }

    function handleDragEnd() {
        setDragIndex(null);
    }

    function handleFileInput(files) {
        const filteredFiles = Array.from(files)
            .filter(f => f.type.startsWith("image/"));
        addImages(filteredFiles);
    }

    function handleDrop(e) {
        e.preventDefault();
        handleFileInput(e.dataTransfer.files);
    }

    function handleDragOver(e) {
        e.preventDefault();
    }

    return {
        images,
        setImages,
        handleFileInput,
        handleDrop,
        handleDragOver,
        removeImage,
        handleDragStart,
        handleDragEnter,
        handleDragEnd
    };
}