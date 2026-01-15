import React from "react";
import { Dialog } from "primereact/dialog";
import { Galleria } from "primereact/galleria";
import { Button } from "primereact/button";
import {
  buildImageUrl,
  updateMainImage,
  deleteProductImage,
} from "./productService";

const ImageGallery = ({ product, visible, onHide, onUpdated }) => {
  if (!product) return null;

  const images = product.imageUrls.map(buildImageUrl);

  const setMain = async (fullUrl) => {
    const path = fullUrl.replace("http://localhost:8080", "");
    await updateMainImage(product.id, path);
    onUpdated();
  };

  const remove = async (fullUrl) => {
    const path = fullUrl.replace("http://localhost:8080", "");
    await deleteProductImage(path);
    onUpdated();
  };

  return (
    <Dialog visible={visible} onHide={onHide} style={{ width: "60vw" }}>
      <Galleria
        value={images}
        showThumbnails
        showItemNavigators
        item={(item) => (
          <div className="flex flex-column align-items-center gap-2">
            <img src={item} style={{ maxWidth: "100%" }} />
            <div className="flex gap-2">
              <Button label="Ảnh đại diện" onClick={() => setMain(item)} />
              <Button
                label="Xoá"
                severity="danger"
                onClick={() => remove(item)}
              />
            </div>
          </div>
        )}
      />
    </Dialog>
  );
};

export default ImageGallery;
