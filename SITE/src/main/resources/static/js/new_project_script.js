const objectives = [];
    const objectivesList = document.getElementById('objectivesList');
    const objectiveInput = document.getElementById('objectiveInput');
    const hiddenObjectives = document.getElementById('hiddenObjectives');
    const addButton = document.getElementById('addObjectiveBtn');
    const titleInput = document.getElementById('title');
    const descriptionInput = document.getElementById('description');
    const fileInput = document.getElementById("fileUpload");
    const fileList = document.getElementById("fileList");
    const maxStudentsContainer = document.getElementById("maxStudentsContainer");
    const typeSelect = document.getElementById("type");
    const submitButton = document.getElementById("submitButton");
    const errorMessageContainer = document.getElementById("errorMessage");

    const dataTransfer = new DataTransfer();

    typeSelect.addEventListener('change', function() {
        if (this.value === "GROUP") {
            maxStudentsContainer.style.display = "block";
        } else {
            maxStudentsContainer.style.display = "none";
        }
    });

    function updateHiddenField() {
        hiddenObjectives.value = objectives.join('\n');
    }

    function createObjectiveTag(text) {
        const tag = document.createElement('div');
        tag.className = 'objective-tag';
        tag.innerHTML = `
            ${text}
            <span class="tag-remove-btn" onclick="removeObjective('${text}')">
                <i class="bi bi-x-circle"></i>
            </span>
        `;
        return tag;
    }

    function addObjective() {
        const text = objectiveInput.value.trim();
        if (text && !objectives.includes(text)) {
            objectives.push(text);
            objectivesList.appendChild(createObjectiveTag(text));
            objectiveInput.value = '';
            updateHiddenField();
        }
    }

    function removeObjective(text) {
        const index = objectives.indexOf(text);
        if (index > -1) {
            objectives.splice(index, 1);
            objectivesList.children[index].remove();
            updateHiddenField();
        }
    }

    addButton.addEventListener('click', addObjective);
    objectiveInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            e.preventDefault();
            addObjective();
        }
    });

    function validateInputLimits(inputElement, maxWords, maxChars) {
    inputElement.addEventListener('input', function () {
        let words = this.value.split(/\s+/).filter(word => word.length > 0);
        let charCount = this.value.length;

        if (charCount > maxChars) {
            alert(`Maximum character limit is ${maxChars}. Please shorten your text.`);
            this.value = this.value.substring(0, maxChars);
        } else if (words.length > maxWords) {
            alert(`Maximum word limit is ${maxWords}. Please shorten your text.`);
            this.value = words.slice(0, maxWords).join(' ');
        }
    });
}

function updateFileList() {
    Array.from(fileInput.files).forEach(file => {
        if (!Array.from(dataTransfer.files).some(f => f.name === file.name)) {
            dataTransfer.items.add(file);
        }
    });

    fileInput.files = dataTransfer.files;

    renderFileList();
}

function renderFileList() {
    fileList.innerHTML = "";

    Array.from(dataTransfer.files).forEach((file, index) => {
        const listItem = document.createElement("li");
        listItem.className = "list-group-item d-flex justify-content-between align-items-center";
        listItem.innerHTML = `
            ${file.name}
            <button type="button" class="btn btn-sm btn-danger" onclick="removeFile(${index})">
                <i class="bi bi-x-circle"></i>
            </button>
        `;
        fileList.appendChild(listItem);
    });
}

function removeFile(index) {
    dataTransfer.items.remove(index);
    fileInput.files = dataTransfer.files;
    renderFileList();
}

validateInputLimits(titleInput, 50, 50);
validateInputLimits(descriptionInput, 500, 500);

document.getElementById("projectForm").addEventListener("submit", function (event) {
    let errors = [];

    function showError(containerId, message) {
        let container = document.getElementById(containerId);
        if (container) {
            container.innerHTML = `<p class="text-danger">${message}</p>`;
            container.style.display = "block";
        }
    }

    function clearErrors() {
        document.querySelectorAll(".error-message").forEach(el => {
            el.innerHTML = "";
            el.style.display = "none";
        });
    }

    function isCheckboxGroupValid(groupName) {
        return document.querySelectorAll(`input[name='${groupName}']:checked`).length > 0;
    }

    clearErrors();

    if (!isCheckboxGroupValid("category")) {
        errors.push({ id: "categoryError", message: "At least one category must be selected." });
    }

    if (!isCheckboxGroupValid("studyYearRestriction")) {
        errors.push({ id: "studyYearError", message: "At least one study year restriction must be selected." });
    }

    if (!isCheckboxGroupValid("degreeRestriction")) {
        errors.push({ id: "degreeError", message: "At least one degree restriction must be selected." });
    }

    if (!isCheckboxGroupValid("majorRestriction")) {
        errors.push({ id: "majorError", message: "At least one major restriction must be selected." });
    }

    if (errors.length > 0) {
        event.preventDefault();
        errors.forEach(error => showError(error.id, error.message));
    }
});

document.addEventListener('DOMContentLoaded', function() {
    fetch('/data/subcategories.json')
        .then(response => response.json())
        .then(subcategoriesData => {
            document.querySelectorAll('.category-checkbox').forEach(checkbox => {
                checkbox.addEventListener('change', function() {
                    const category = this.dataset.category;
                    const subcategoriesContainer = this.closest('.category-group').querySelector('.subcategories-container');

                    if (this.checked) {

                        const subcategories = subcategoriesData[category] || [];
                        subcategoriesContainer.innerHTML = subcategories.map(sub => `
                            <div class="form-check">
                                <input class="form-check-input"
                                       type="checkbox"
                                       name="subcategories"
                                       id="sub-${sub.replace(/\s+/g, '-')}"
                                       value="${sub}">
                                <label class="form-check-label" for="sub-${sub.replace(/\s+/g, '-')}">
                                    ${sub}
                                </label>
                            </div>
                        `).join('');
                        subcategoriesContainer.style.display = 'block';
                    } else {

                        subcategoriesContainer.innerHTML = '';
                        subcategoriesContainer.style.display = 'none';
                    }
                });
            });
        })
        .catch(error => console.error('Error loading subcategories:', error));
});

fetch('/data/subcategories.json')
    .then(response => {
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      return response.json();
    })
    .then(data => {
      const container = document.getElementById("categoriesContainer");


      for (const category in data) {
        if (data.hasOwnProperty(category)) {
          const categoryGroup = document.createElement('div');
          categoryGroup.className = "category-group mb-2";

          const label = document.createElement('label');
          label.className = "form-check form-check-inline";

          const input = document.createElement('input');
          input.type = "checkbox";
          input.className = "form-check-input category-checkbox";
          input.name = "category";
          input.value = category;
          input.setAttribute('data-category', category);
          label.appendChild(input);

          const span = document.createElement('span');
          span.className = "form-check-label";
          span.textContent = category;
          label.appendChild(span);

          categoryGroup.appendChild(label);

          const subContainer = document.createElement('div');
          subContainer.className = "subcategories-container ms-4";
          subContainer.style.display = "none";

          data[category].forEach(sub => {
            const subLabel = document.createElement('label');
            subLabel.className = "form-check form-check-inline me-2";

            const subInput = document.createElement('input');
            subInput.type = "checkbox";
            subInput.className = "form-check-input subcategory-checkbox";
            subInput.name = "subcategory";
            subInput.value = sub;
            subLabel.appendChild(subInput);

            const subSpan = document.createElement('span');
            subSpan.className = "form-check-label";
            subSpan.textContent = sub;
            subLabel.appendChild(subSpan);

            subContainer.appendChild(subLabel);
          });
          categoryGroup.appendChild(subContainer);
          container.appendChild(categoryGroup);

          input.addEventListener('change', function() {
            subContainer.style.display = this.checked ? "block" : "none";
          });
        }
      }
    })
    .catch(err => console.error('Error loading categories:', err));
