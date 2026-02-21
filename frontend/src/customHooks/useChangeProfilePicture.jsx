import {useRef, useState} from "react";
import axios from "axios";
import {toast} from "react-toastify";

export function useChangeProfilePicture(entityType, entityId, initialImage) {

    const fileInputRef = useRef(null);
    const [preview, setPreview] = useState(initialImage);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const openFilePicker = () => {
        fileInputRef.current?.click();
    };

    const onFileChange = async (e) => {
        const selectedFile = e.target.files[0];
        if (!selectedFile) return;

        const previewURL = URL.createObjectURL(selectedFile);
        setPreview(previewURL);

        if (!entityId) {
            toast.error("Kan profielfoto nog niet uploaden, probeer opnieuw!");
            return;
        }

        setLoading(true);
        setError(null);

        const formData = new FormData();
        formData.append("profilePicture", selectedFile);

        try {
            await axios.patch(
                `http://localhost:8080/${entityType}/${entityId}/profile-picture`,
                formData,
                {
                    headers: {
                        "Content-Type": "multipart/form-data",
                        Authorization: `Bearer ${localStorage.getItem("token")}`
                        },
                    withCredentials: true,
                }
            );

            toast.success("Profielfoto succesvol veranderd!", {
                duration: 3000,
                position: "top-center",
            });
        } catch (err) {
            setError(err);
            toast.error("Foto uploaden mislukt, probeer opnieuw!", {
                duration: 3000,
                position: "top-center",
            });
        } finally {
            setLoading(false);
        }

        return () => URL.revokeObjectURL(previewURL);
    };

    return {
        fileInputRef,
        preview,
        loading,
        error,
        openFilePicker,
        onFileChange,
    };
}