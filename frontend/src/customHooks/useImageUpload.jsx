import { useEffect, useState } from "react";

export default function useImageUpload({ setValue, maxImages = 8, initialImages = [] }) {
    const [images, setImages] = useState(  initialImages.map(img => ({
            file: img.file || null,
            url: img.url || img
        }))
    );

    const [dragIndex, setDragIndex] = useState(null);

    useEffect(() => {
        return () => {
            images.forEach(img => {
                if (img.file) URL.revokeObjectURL(img.file);
            });
        };
    }, [images]);

    function updateImages(updater) {
        setImages(prev => {
            const updated = updater([...prev]);
            setValue("images", updated);
            return updated;
        });
    }

    function addImages(files) {
        updateImages(prev => [...prev, ...files].slice(0, maxImages));
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
        const newFiles = Array.from(files).map(f => ({ file: f, url: null }));
        const filteredFiles = newFiles.filter(img => img.file?.type.startsWith("image/"));
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