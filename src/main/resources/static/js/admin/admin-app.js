(function (){
    var app = angular.module('app', ['ngRoute', 'paginationModule']);
    app.config(function($routeProvider) {
        $routeProvider
            .when('/home', {
                templateUrl: "/assets/admin/home/home.html",
                controller: "HomeController"
            })
            .when('/products/edit/:id', {
                templateUrl: "/assets/admin/product/product-edition.html",
                controller: "ProductController"
            })
            .when('/products/:action', {
                templateUrl: function(params) {
                    return params.action === "list"
                        ? "/assets/admin/product/product-list.html"
                        : "/assets/admin/product/product-edition.html";
                },
                controller: "ProductController"
            })
            .when('/users/edit/:id', {
                templateUrl: "/assets/admin/user/user-edition.html",
                controller: "UserController"
            })
            .when('/users/:action', {
                templateUrl: function(params) {
                    return params.action === "list"
                        ? "/assets/admin/user/user-list.html"
                        : "/assets/admin/user/user-edition.html";
                },
                controller: "UserController"
            })
            .when('/categories', { templateUrl: '/assets/admin/category/category.html', controller: 'CategoryController' })
            .when('/orders', { templateUrl: '/assets/admin/order/order.html', controller: 'OrderController' })
            .otherwise({ redirectTo: '/home' });

    });
    app.controller('MainController', function($scope, $rootScope, $location, SearchService) {
        $scope.searchKeyword = '';
        $scope.isActive = function(route) {
            return $location.path() === route;
        };
        $scope.search = function() {
            console.log("Keyword:", $scope.searchKeyword);
            SearchService.search($location.path(), $scope.searchKeyword)
                .then(items => {
                    console.log("Kết quả search:", items); // Xem API trả về gì
                    $rootScope.$broadcast('searchResults' + $location.path(), items);
                })
                .catch(error => {
                    console.log("Lỗi gọi API:", error);
                });
        };
    });
    app.factory('SearchService', function($http) {
        return {
            search: function(route, keyword) {

                var apiUrl = '';
                switch (route) {
                    case '/products/edit':
                    case '/products/list':
                        apiUrl = 'http://localhost:8080/admin/products';
                        break;
                    case '/categories':
                        apiUrl = 'http://localhost:8080/admin/categories';
                        break;
                    case '/users/edit':
                    case '/users/list':
                        apiUrl = 'http://localhost:8080/admin/users';
                        break;
                    case '/orders':
                        apiUrl = 'http://localhost:8080/admin/orders';
                        break;
                    case '/shop':
                        apiUrl = 'http://localhost:8080/home/index/shop';
                        break;
                    default:
                        return Promise.resolve([]);
                }
                var url = keyword && keyword.trim() !== ""
                    ? `${apiUrl}?keyword=${encodeURIComponent(keyword)}`
                    : apiUrl;
                return $http.get(url)
                    .then(response => response.data)
                    .catch(error => {
                        console.log('Search error:', error);
                        return [];
                    });
            }
        };
    });

    // app.factory('PaginationService', function() {
    //     return {
    //         createPager: function(itemsPerPage) {
    //             return {
    //                 page: 0,
    //                 pageSize: itemsPerPage || 8,
    //                 items: [],
    //                 setItems: function(data) {
    //                     this.items = data;
    //                 },
    //                 get pagedItems() {
    //                     var start = this.page * this.pageSize;
    //                     return this.items.slice(start, start + this.pageSize);
    //                 },
    //                 get count() {
    //                     return Math.ceil(this.items.length / this.pageSize);
    //                 },
    //                 first() { this.page = 0; },
    //                 last() { this.page = this.count - 1; },
    //                 next() {
    //                     if (this.page < this.count - 1) this.page++;
    //                 },
    //                 previous() {
    //                     if (this.page > 0) this.page--;
    //                 }
    //             };
    //         },
    //         sortBy: function(items, column, reverse) {
    //             return items.slice().sort((a, b) => {
    //                 let valA = column.includes('.') ? column.split('.').reduce((obj, key) => obj?.[key], a) : a[column];
    //                 let valB = column.includes('.') ? column.split('.').reduce((obj, key) => obj?.[key], b) : b[column];
    //
    //                 if (!isNaN(valA) && !isNaN(valB)) {
    //                     valA = Number(valA);
    //                     valB = Number(valB);
    //                 } else {
    //                     if (typeof valA === "string") valA = valA.toLowerCase();
    //                     if (typeof valB === "string") valB = valB.toLowerCase();
    //                 }
    //
    //                 if (valA === valB) return 0;
    //                 return (valA < valB) ? (reverse ? 1 : -1) : (reverse ? -1 : 1);
    //             });
    //         }
    //     };
    // });
    app.filter('passwordFormat', function() {
        return function(input) {
            if (input && input.length > 10) {
                let p = input.split('').reverse().join('').substring(0, 3);
                return input.substring(0, 5) + '...' + p.split('').reverse().join('');
            }
            return input;
        };
    });

})();