document.addEventListener("DOMContentLoaded", () => {
  const typeSelect = document.getElementById("type");
  const maxStudentsContainer = document.getElementById("maxStudentsContainer");
  const maxStudentsInput = document.getElementById("maxStudents");

  const toggleMaxStudentsField = () => {
    if (typeSelect.value === "GROUP") {
      maxStudentsContainer.style.display = "block";
      maxStudentsInput.required = true;
    } else {
      maxStudentsContainer.style.display = "none";
      maxStudentsInput.required = false;
      maxStudentsInput.value = "1";
    }
  };

  toggleMaxStudentsField();
  typeSelect.addEventListener("change", toggleMaxStudentsField);

  const statusSelect = document.getElementById("status");
  const editableFields = document.getElementById("editableFields");

  const toggleEditableFields = () => {
    if (statusSelect.value === "CLOSED") {
      editableFields.style.display = "none";
    } else {
      editableFields.style.display = "block";
    }
  };

  toggleEditableFields();
  statusSelect.addEventListener("change", toggleEditableFields);

  function showError(groupName, message) {
    const input = document.querySelector(`input[name='${groupName}']`);
    if (input) {
      const container = input.closest(".mb-4");
      if (container) {
        let errorDiv = container.querySelector(".error-message");
        if (!errorDiv) {
          errorDiv = document.createElement("div");
          errorDiv.className = "error-message text-danger mb-2";
          container.insertBefore(errorDiv, container.firstChild);
        }
        errorDiv.textContent = message;
      }
    }
  }

  function clearError(groupName) {
    const input = document.querySelector(`input[name='${groupName}']`);
    if (input) {
      const container = input.closest(".mb-4");
      if (container) {
        const errorDiv = container.querySelector(".error-message");
        if (errorDiv) {
          errorDiv.textContent = "";
        }
      }
    }
  }

  function showErrorForField(fieldId, message) {
    const field = document.getElementById(fieldId);
    if (field) {
      const container = field.closest(".mb-4");
      if (container) {
        let errorDiv = container.querySelector(".error-message");
        if (!errorDiv) {
          errorDiv = document.createElement("div");
          errorDiv.className = "error-message text-danger mb-2";
          container.insertBefore(errorDiv, field.nextSibling);
        }
        errorDiv.textContent = message;
      }
    }
  }

  function clearErrorForField(fieldId) {
    const field = document.getElementById(fieldId);
    if (field) {
      const container = field.closest(".mb-4");
      if (container) {
        const errorDiv = container.querySelector(".error-message");
        if (errorDiv) {
          errorDiv.textContent = "";
        }
      }
    }
  }

  const titleInput = document.getElementById("title");
  const descriptionInput = document.getElementById("description");
  const titleMax = 50;
  const descriptionMax = 500;

  titleInput.setAttribute("maxlength", titleMax);
  descriptionInput.setAttribute("maxlength", descriptionMax);

  titleInput.addEventListener("input", () => {
    if (titleInput.value.length >= titleMax) {
      showErrorForField(
        "title",
        "Maximum title length reached. You cannot type more."
      );
    } else {
      clearErrorForField("title");
    }
  });

  descriptionInput.addEventListener("input", () => {
    if (descriptionInput.value.length >= descriptionMax) {
      showErrorForField(
        "description",
        "Maximum description length reached. You cannot type more."
      );
    } else {
      clearErrorForField("description");
    }
  });

  const form = document.querySelector("form");
  form.addEventListener("submit", (event) => {
    let hasError = false;

    clearErrorForField("title");
    clearErrorForField("description");

    if (titleInput.value.length > titleMax) {
      showErrorForField("title", "Title cannot be greater than 50 characters.");
      hasError = true;
    }
    if (descriptionInput.value.length > descriptionMax) {
      showErrorForField(
        "description",
        "Description cannot be greater than 500 characters."
      );
      hasError = true;
    }

    const groups = [
      "category",
      "studyYearRestriction",
      "degreeRestriction",
      "majorRestriction",
    ];
    groups.forEach((group) => {
      clearError(group);
      if (
        document.querySelectorAll(`input[name='${group}']:checked`).length === 0
      ) {
        let errorMsg = "";
        switch (group) {
          case "category":
            errorMsg = "At least one category must be selected.";
            break;
          case "studyYearRestriction":
            errorMsg = "At least one study year restriction must be selected.";
            break;
          case "degreeRestriction":
            errorMsg = "At least one degree restriction must be selected.";
            break;
          case "majorRestriction":
            errorMsg = "At least one major restriction must be selected.";
            break;
        }
        showError(group, errorMsg);
        hasError = true;
      }
    });

    if (hasError) {
      event.preventDefault();
    }
  });

  const deleteBtns = document.querySelectorAll(".delete-existing");

  deleteBtns.forEach(function (btn) {
    btn.addEventListener("click", function () {
      const deliverableId = btn.getAttribute("data-file-id");
      const deliverableName = btn.getAttribute("data-file-name");

      if (!deliverableName) {
        alert("File name not found!");
        return;
      }

      if (
        confirm(`Are you sure you want to delete the file: ${deliverableName}?`)
      ) {
        fetch(`/staff/projects/delete-file/${deliverableId}`, {
          method: "DELETE",
          headers: { "Content-Type": "application/json" },
          credentials: "same-origin",
        })
          .then((response) => {
            if (!response.ok) {
              return response.text().then((text) => {
                throw new Error(text);
              });
            }
            return response.text();
          })
          .then((message) => {
            console.log("Success:", message);
            btn.closest("li").remove();
          })
          .catch((error) => {
            console.error("Error:", error);
            alert(`An error occurred: ${error.message}`);
          });
      }
    });
  });

  function getProjectIdFromURL() {
    const urlParts = window.location.pathname.split("/");
    const editIndex = urlParts.indexOf("edit");
    if (editIndex !== -1 && urlParts.length > editIndex + 1) {
      return urlParts[editIndex + 1];
    }
    return null;
  }

  document.getElementById("fileInput").addEventListener("change", function () {
    const files = this.files;
    const newFileList = document.getElementById("newFileList");

    Array.from(files).forEach((file) => {
      if (!file.name.endsWith(".pdf")) {
        alert("Only PDF files are allowed.");
        return;
      }

      const li = document.createElement("li");
      li.className =
        "list-group-item d-flex justify-content-between align-items-center";
      li.innerHTML = `${file.name} 
          <button type="button" class="btn btn-sm btn-success upload-file" data-file-name="${file.name}">
              <i class="bi bi-upload"></i>
          </button>`;

      newFileList.appendChild(li);

      li.querySelector(".upload-file").addEventListener("click", function () {
        uploadFile(file, li);
      });
    });
  });

  function uploadFile(file, listItem) {
    const projectId = getProjectIdFromURL();
    if (!projectId) {
      console.error("Project ID not found in URL!");
      alert("Project ID not found. Cannot upload file.");
      return;
    }

    const formData = new FormData();
    formData.append("file", file);

    fetch(`/staff/projects/upload/${projectId}`, {
      method: "POST",
      body: formData,
    })
      .then((response) => response.json())
      .then((data) => {
        if (data.success) {
          listItem.innerHTML = `${file.name} <span class="text-success">Uploaded</span>`;
        } else {
          alert("File upload failed: " + data.message);
        }
      })
      .catch((error) => {
        console.error("Upload error:", error);
        alert("An error occurred while uploading the file.");
      });
  }

  fetch("/data/subcategories.json")
      .then((response) => {
        if (!response.ok) {
          throw new Error("Network response was not ok");
        }
        return response.json();
      })
      .then((data) => {
        const container = document.getElementById("categoriesContainer");

        const errorDiv = document.getElementById("categoryError");

        container.innerHTML = "";

        for (const category in data) {
          if (data.hasOwnProperty(category)) {
            const categoryGroup = document.createElement("div");
            categoryGroup.className = "category-group mb-2";

            const label = document.createElement("label");
            label.className = "form-check form-check-inline";

            const categoryCheckbox = document.createElement("input");
            categoryCheckbox.type = "checkbox";
            categoryCheckbox.className = "form-check-input category-checkbox";
            categoryCheckbox.name = "category";
            categoryCheckbox.value = category;
            categoryCheckbox.setAttribute("data-category", category);
            label.appendChild(categoryCheckbox);

            const span = document.createElement("span");
            span.className = "form-check-label";
            span.textContent = category;
            label.appendChild(span);

            categoryGroup.appendChild(label);

            const subContainer = document.createElement("div");
            subContainer.className = "subcategories-container ms-4";
            subContainer.style.display = "none";

            data[category].forEach((sub) => {
              const subLabel = document.createElement("label");
              subLabel.className = "form-check form-check-inline me-2";

              const subInput = document.createElement("input");
              subInput.type = "checkbox";
              subInput.className = "form-check-input subcategory-checkbox";
              subInput.name = "subcategories";
              subInput.value = sub;
              subInput.setAttribute("data-category", category);

              if (
                typeof savedSubcategories !== "undefined" &&
                Array.isArray(savedSubcategories) &&
                savedSubcategories.includes(sub)
              ) {
                subInput.checked = true;
              }
              subLabel.appendChild(subInput);

              const subSpan = document.createElement("span");
              subSpan.className = "form-check-label";
              subSpan.textContent = sub.split(":")[0];
              subLabel.appendChild(subSpan);

              subContainer.appendChild(subLabel);
            });

            categoryGroup.appendChild(subContainer);
            container.appendChild(categoryGroup);

            categoryCheckbox.addEventListener("change", function () {
              if (this.checked) {
                subContainer.style.display = "block";
              } else {
                subContainer.style.display = "none";
                subContainer.querySelectorAll(".subcategory-checkbox").forEach((subCheckbox) => {
                  subCheckbox.checked = false;
                });
              }
              validateCategorySelection();
            });

            subContainer.addEventListener("change", validateCategorySelection);

            if (
              typeof savedSubcategories !== "undefined" &&
              Array.isArray(savedSubcategories)
            ) {
              const anySubSaved = data[category].some((sub) =>
                savedSubcategories.includes(sub)
              );
              if (anySubSaved) {
                categoryCheckbox.checked = true;
                subContainer.style.display = "block";
              }
            }
          }
        }
      })
      .catch((err) => console.error("Error loading categories:", err));

      function validateCategorySelection() {
        const selectedCategories = document.querySelectorAll(".category-checkbox:checked");
        let valid = true;

        selectedCategories.forEach((categoryCheckbox) => {
            const category = categoryCheckbox.value;
            const subcategoryCheckboxes = document.querySelectorAll(
                `.subcategory-checkbox[data-category="${category}"]:checked`
            );

            if (subcategoryCheckboxes.length === 0) {
                valid = false;
            }
        });

        const errorDiv = document.getElementById("categoryError");

        if (!valid) {
            errorDiv.textContent = "Please select at least one subcategory for each chosen category.";
            errorDiv.style.display = "block";
        } else {
            errorDiv.style.display = "none";
        }
    }

    document.querySelector("form").addEventListener("submit", function (event) {
      validateCategorySelection();
      if (document.getElementById("categoryError").style.display === "block") {
          event.preventDefault();
      }
    });

  });