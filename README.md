#  FakeStore E-Commerce App

An Android shopping application built using **Kotlin** and **Jetpack Compose**, powered by the [Fake Store API](https://api.escuelajs.co/api/v1).  
This project demonstrates **modern Android development practices** including **Paging 3**, **Room**, **Retrofit**, and **Material Design**.

---

##  Features

### 1. Product List (with Pagination)
- Displays products from `/products?offset=0&limit=10` using **Paging 3**.
- Shows loading state placeholders and error messages.
- Each product card contains:
  - Product image
  - Title
  - Price
  - **Add to Cart** button with Snackbar feedback.

---

### 2. Categories
- Loads categories from `/categories`.
- Each category is shown as a card with its **name** and **image**.
- Tap a category to view products filtered by that category via `/categories/{id}/products`.

---

### 3. Product Details
- Displays:
  - Product image
  - Title
  - Price
  - Full description
- Data fetched from `/products/{id}`.

---

### 4. Local Cart
- Users can add products to a **local Room database**.
- Cart screen displays:
  - All added products with name, image, price, and quantity.
  - Total price calculated dynamically.
- Items persist even after app restart.

---

##  Tech Stack

| Layer        | Technology |
|--------------|------------|
| **UI**       | Jetpack Compose + Material 3 |
| **Navigation** | Navigation Component for Compose |
| **Networking** | Retrofit + OkHttp + kotlinx.serialization |
| **Paging**   | Paging 3 |
| **Local Storage** | Room Database |
| **Async**    | Kotlin Coroutines & Flow |
| **UI Feedback** | Snackbars for actions & errors |

---

##  API Endpoints Used

| Feature | Endpoint | Method |
|---------|----------|--------|
| List products (paginated) | `/products?offset=0&limit=10` | GET |
| Product details | `/products/{id}` | GET |
| List categories | `/categories` | GET |
| Products by category | `/categories/{id}/products` | GET |

**Base URL:** `https://api.escuelajs.co/api/v1`

---



