document.addEventListener("DOMContentLoaded", function () {
    const sidebar = document.getElementById("sidebar");
    const menuToggle = document.querySelector("[data-menu-toggle]");

    if (sidebar && menuToggle) {
        menuToggle.addEventListener("click", function () {
            sidebar.classList.toggle("open");
        });

        document.addEventListener("click", function (event) {
            if (window.innerWidth <= 980
                && sidebar.classList.contains("open")
                && !sidebar.contains(event.target)
                && !menuToggle.contains(event.target)) {
                sidebar.classList.remove("open");
            }
        });

        sidebar.querySelectorAll("a").forEach(function (link) {
            link.addEventListener("click", function () {
                if (window.innerWidth <= 980) {
                    sidebar.classList.remove("open");
                }
            });
        });
    }

    document.querySelectorAll("[data-confirm]").forEach(function (element) {
        element.addEventListener("click", function (event) {
            if (!window.confirm(element.getAttribute("data-confirm"))) {
                event.preventDefault();
                event.stopImmediatePropagation();
            }
        });
    });

    const itemsTable = document.querySelector("[data-items-table]");
    if (itemsTable) {
        const tbody = itemsTable.querySelector("tbody");
        const addButton = document.querySelector("[data-add-item]");
        const form = document.querySelector("form[data-recipe-form]");

        const reindex = function () {
            const rows = Array.from(tbody.querySelectorAll("tr"));
            rows.forEach(function (row, index) {
                row.querySelectorAll("input, select, textarea").forEach(function (input) {
                    if (input.name) {
                        input.name = input.name.replace(/items\[\d+\]/g, "items[" + index + "]");
                    }
                    if (input.id) {
                        input.id = input.id.replace(/items\d+\./g, "items" + index + ".");
                    }
                });
            });
        };

        if (addButton) {
            addButton.addEventListener("click", function () {
                const firstRow = tbody.querySelector("tr");
                if (!firstRow) {
                    return;
                }
                const clone = firstRow.cloneNode(true);
                clone.querySelectorAll("input, select, textarea").forEach(function (input) {
                    input.value = "";
                });
                tbody.appendChild(clone);
                reindex();
                const firstInput = clone.querySelector("input");
                if (firstInput) {
                    firstInput.focus();
                }
            });
        }

        tbody.addEventListener("click", function (event) {
            const removeButton = event.target.closest("[data-remove-item]");
            if (!removeButton) {
                return;
            }
            if (tbody.querySelectorAll("tr").length <= 1) {
                window.alert("Debe mantener al menos un medicamento en la receta.");
                return;
            }
            removeButton.closest("tr").remove();
            reindex();
        });

        if (form) {
            form.addEventListener("submit", reindex);
        }
    }
});
