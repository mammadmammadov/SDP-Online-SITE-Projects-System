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