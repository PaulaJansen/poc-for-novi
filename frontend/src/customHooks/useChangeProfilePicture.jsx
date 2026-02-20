import {useRef, useState} from "react";
import axios from "axios";
import {toast} from "react-toastify";

export function useChangeProfilePicture(entityType, entityId, initialImage) {

    const fileInputRef = useRef(null);
    const [file, setFile] = useState(null);
    const [preview, setPreview] = useState(initialImage);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const openFilePicker = () => {
        fileInputRef.current?.click();
    };

    const onFileChange = (e) => {
        const selectedFile = e.target.files[0];
        if (!selectedFile) return;

        setFile(selectedFile);
        const previewURL = URL.createObjectURL(selectedFile);
        setPreview(previewURL);

        return () => URL.revokeObjectURL(previewURL);
    };

    const upload = async () => {
        if (!file) return;

        setLoading(true);
        setError(null);

        const formData = new FormData();
        formData.append("profilePicture", file);

        try {
            await axios.patch(
                `http://localhost:8080/${entityType}/${entityId}/profile-picture`,
                formData,
                {withCredentials: true}
            );

            setFile(null);
            toast.success("Profielfoto succesvol veranderd!",
                {
                    duration: 3000,
                    position: "top-center",
                });
        } catch (e) {
            setError(e);
            toast.error("Foto uploaden mislukt, probeer opnieuw!",
                {
                    duration: 3000,
                    position: "top-center",
                })
        } finally {
            setLoading(false);
        }
    };

    return {
        fileInputRef,
        preview,
        loading,
        error,
        openFilePicker,
        onFileChange,
        upload,
    };
}