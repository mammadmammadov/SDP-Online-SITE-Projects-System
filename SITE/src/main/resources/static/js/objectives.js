const objectivesList = document.getElementById("objectivesList");
const objectiveInput = document.getElementById("objectiveInput");
const hiddenObjectives = document.getElementById("hiddenObjectives");
const addButton = document.getElementById("addObjectiveBtn");
let objectives = hiddenObjectives.value ? hiddenObjectives.value.split(",") : [];
 function updateHiddenField() {
          hiddenObjectives.value = objectives.join(",");
      }

  function createObjectiveTag(text) {
          const tag = document.createElement("span");
          tag.className = "objective-tag";
          tag.innerHTML = `
              <span>${text}</span>
              <span class="tag-remove-btn" onclick="removeObjective(this)">
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
              objectiveInput.value = "";
              updateHiddenField();
          }
      }

      window.removeObjective = function (element) {
          const text = element.parentElement.textContent.trim();
          const index = objectives.indexOf(text);
          if (index > -1) {
              objectives.splice(index, 1);
              element.parentElement.remove();
              updateHiddenField();
          }
      };

      addButton.addEventListener("click", addObjective);
      objectiveInput.addEventListener("keypress", (e) => {
          if (e.key === "Enter") {
              e.preventDefault();
              addObjective();
          }
      });

objectives.forEach(obj => objectivesList.appendChild(createObjectiveTag(obj)));
