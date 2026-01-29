import {useRef, useState} from "react";
import axios from "axios";

export function useChangeProfilePicture(artistId, initialImage) {

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
        setPreview(URL.createObjectURL(selectedFile));
    };

    const upload = async () => {
        if (!file) return;

        setLoading(true);
        setError(null);

        const formData = new FormData();
        formData.append("profilePicture", profilePictureFile);

        try {
            await axios.patch(
                `http://localhost:8080/artists/${artistId}/profile-picture`,
                formData,
                {withCredentials: true}
            );

            setFile(null);
        } catch (e) {
            setError(e);
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