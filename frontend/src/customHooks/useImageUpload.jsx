import { useEffect, useState } from "react";

export default function useImageUpload({ setValue, maxImages = 8 }) {
    const [images, setImages] = useState([]);
    const [dragIndex, setDragIndex] = useState(null);

    useEffect(() => {
        return () => {
            images.forEach(file => URL.revokeObjectURL(file));
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
        if (!files) return;
        addImages(
            Array.from(files ?? []).filter(f => f.type.startsWith("image/"))
        );
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
        handleFileInput,
        handleDrop,
        handleDragOver,
        removeImage,
        handleDragStart,
        handleDragEnter,
        handleDragEnd
    };
}