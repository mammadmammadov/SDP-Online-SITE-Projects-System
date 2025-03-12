document.addEventListener("DOMContentLoaded", function () {
  document.querySelector("form").addEventListener("submit", function (e) {
    const currentTotal =
      parseFloat(document.getElementById("totalWeightage").value) || 0;
    const newWeightage =
      parseFloat(document.getElementById("weightage").value) || 0;

    if (currentTotal + newWeightage > 100) {
      alert(
        "Total weightage cannot exceed 100%. Current total: " +
          currentTotal +
          "%"
      );
      e.preventDefault();
    }
  });
});
