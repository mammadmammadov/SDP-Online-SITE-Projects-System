document.addEventListener("DOMContentLoaded", function () {
  let typeSelect = document.getElementById("type");
  let maxStudentsContainer = document.getElementById("maxStudentsContainer");
  let maxStudentsInput = document.getElementById("maxStudents");

  function toggleMaxStudentsField() {
    if (typeSelect.value === "GROUP") {
      maxStudentsContainer.style.display = "block";
      maxStudentsInput.required = true;
    } else {
      maxStudentsContainer.style.display = "none";
      maxStudentsInput.required = false;
      maxStudentsInput.value = "1";
    }
  }

  toggleMaxStudentsField();

  typeSelect.addEventListener("change", function () {
    toggleMaxStudentsField();

    if (typeSelect.value === "INDIVIDUAL") {
      maxStudentsInput.value = "1";
    }
  });
});
