document.addEventListener('DOMContentLoaded', function() {
        document.querySelectorAll('.card-clickable').forEach(function(clickableArea) {
          clickableArea.addEventListener('click', function(e) {
            if (e.target.closest('a, button')) {
              return;
            }
            const card = this.closest('.card');
            const infoButton = card.querySelector('button[data-bs-toggle="modal"]');
            if (infoButton) {
              const target = infoButton.getAttribute('data-bs-target');
              const modal = new bootstrap.Modal(document.querySelector(target));
              modal.show();
            }
          });
        });
      });