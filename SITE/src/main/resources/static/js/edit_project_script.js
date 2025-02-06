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
});
