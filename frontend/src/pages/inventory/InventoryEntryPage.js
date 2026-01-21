// import React, { useState, useEffect } from "react";
// import { DataTable } from "primereact/datatable";
// import { Column } from "primereact/column";
// import { Button } from "primereact/button";
// import InventoryService from "./inventoryService";
// import InventoryForm from "./InventoryForm";
// import "./inventory.css";

// export default function InventoryEntryPage() {
//   const [products, setProducts] = useState([]);
//   const [suppliers, setSuppliers] = useState([]);
//   const [items, setItems] = useState([]);

//   const [formVisible, setFormVisible] = useState(false);
//   const [formAction, setFormAction] = useState("IMPORT"); // IMPORT / EXPORT

//   useEffect(() => {
//     loadProducts();
//     loadSuppliers();
//     loadItems();
//   }, []);

//   const loadProducts = () => {
//     InventoryService.getAllProducts()
//       .then(data => setProducts(data))
//       .catch(err => console.error("Lỗi load products:", err));
//   };

//   const loadSuppliers = () => {
//     InventoryService.getAllSuppliers()
//    .then(data => setSuppliers(data))
//       .catch(err => console.error("Lỗi load suppliers:", err));
//   };

// const loadItems = () => {
//   InventoryService.getAllInventoryItems()
//     .then(data => {
//       console.log("Dữ liệu InventoryItems:", data); // check dữ liệu thật

//       const normalized = data.map(item => ({
//         ...item,
//         batchNumber: item.batchNumber || item.batchCode,       // đồng bộ batch
//         entryDate: item.entryDate || item.date,                // đồng bộ ngày nhập
//         expirationDate: item.expirationDate || item.expDate,  // đồng bộ hạn SD
//         productName: item.productName || item.product?.name,  // đồng bộ tên sản phẩm
//         supplierName: item.supplierName || item.supplier?.name,
//         warehouseName: item.warehouseName || item.warehouse
//       }));

//       setItems(normalized);
//     })
//     .catch(err => console.error("Lỗi load inventory items:", err));
// };


//   const openForm = (actionType) => {
//     setFormAction(actionType);
//     setFormVisible(true);
//   };

//   const closeForm = () => {
//     setFormVisible(false);
//     loadItems(); // reload dữ liệu sau khi đóng form
//   };

  
//   const formatDate = value => (value ? new Date(value).toLocaleDateString() : "");

//   return (
//     <div className="inventory-page">
//       <h1 className="inventory-title">Quản lý kho</h1>

//       <div className="inventory-actions p-mb-3">
//         <Button label="Nhập kho" className="p-button-success p-mr-2" onClick={() => openForm("IMPORT")} />
//         <Button label="Xuất kho" className="p-button-danger" onClick={() => openForm("EXPORT")} />
//       </div>

//       <div className="inventory-table-section">
//         <h2>Danh sách hàng nhập gần đây</h2>
//         <DataTable
//           value={items}
//           paginator
//           rows={10}
//           emptyMessage="Không có dữ liệu"
//           responsiveLayout="scroll"
//         >

//        <Column field="batchNumber" header="Batch" />
// <Column field="productName" header="Tên sản phẩm" />
// <Column field="quantity" header="Số lượng" />
// <Column
//   field="entryDate"
//   header="Ngày nhập"
//   body={row => formatDate(row.entryDate)}
// />
// <Column
//   field="expirationDate"
//   header="Hạn SD"
//   body={row => formatDate(row.expirationDate)}
// />


     
//           <Column field="status" header="Trạng thái" />
//           <Column field="supplierName" header="Nhà cung cấp" />
//           <Column field="warehouseName" header="Kho" />
//         </DataTable>
//       </div>


//       {formVisible && (
//         <InventoryForm
//           visible={formVisible}
//         type={formAction}
//           onClose={closeForm}
//           products={products}
//           suppliers={suppliers}
//         />
//       )}
//     </div>
//   );
// }
