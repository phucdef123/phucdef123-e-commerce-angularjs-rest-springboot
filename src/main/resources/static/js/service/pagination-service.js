angular.module('paginationModule', []).factory('PaginationService', function() {
    return {
        createPager: function(itemsPerPage) {
            return {
                page: 0,
                pageSize: itemsPerPage || 8,
                items: [],
                setItems: function(data) {
                    this.items = data;
                },
                get pagedItems() {
                    var start = this.page * this.pageSize;
                    return this.items.slice(start, start + this.pageSize);
                },
                get count() {
                    return Math.ceil(this.items.length / this.pageSize);
                },
                first() { this.page = 0; },
                last() { this.page = this.count - 1; },
                next() {
                    if (this.page < this.count - 1) this.page++;
                },
                previous() {
                    if (this.page > 0) this.page--;
                }
            };
        },
        sortBy: function(items, column, reverse) {
            return items.slice().sort((a, b) => {
                let valA = column.includes('.') ? column.split('.').reduce((obj, key) => obj?.[key], a) : a[column];
                let valB = column.includes('.') ? column.split('.').reduce((obj, key) => obj?.[key], b) : b[column];

                if (!isNaN(valA) && !isNaN(valB)) {
                    valA = Number(valA);
                    valB = Number(valB);
                } else {
                    if (typeof valA === "string") valA = valA.toLowerCase();
                    if (typeof valB === "string") valB = valB.toLowerCase();
                }

                if (valA === valB) return 0;
                return (valA < valB) ? (reverse ? 1 : -1) : (reverse ? -1 : 1);
            });
        }
    };
});
