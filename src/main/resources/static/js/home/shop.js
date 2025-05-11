(function () {
    var app = angular.module('shopApp', ['paginationModule','ngCookies', 'cartModule']);
    app.controller('shopCtrl', function ($scope, $rootScope, $http, PaginationService, $timeout, CartService, $cookies) {
        $scope.listProductsByCategory = [];
        $scope.listProducts = [];
        $scope.listCategories = [];
        $scope.sortColumn = "id";
        $scope.reverse = false;// false = tăng dần
        $scope.product = {};
        $scope.user = {};

        var host = "http://localhost:8080/home/index";
        var urlImg = "http://localhost:8080/uploads/images";

        $scope.loadAllProducts = function () {
            $http.get(`${host}/shop`).then(response => {
                $scope.listProducts = response.data;
                $scope.listProducts.forEach(p => {
                    if (p.image) {
                        p.image = `${urlImg}/${p.image}`;
                    }
                });
                $scope.pager.setItems(PaginationService.sortBy($scope.listProducts, $scope.sortColumn, $scope.reverse));
                console.log('listProducts:',response.data);
            }).catch(error => {
                console.log(error);
            })
        }
        $scope.goToDetail = function(productId) {
            window.location.href = `/shop/detail/${productId}`;
        };
        $scope.loadUserDetails = function () {
            $http.get('http://localhost:8080/auth/details').then(response => {
                if (response.status !== 200) {
                    console.log("⛔ User chưa đăng nhập hoặc không có dữ liệu.");
                    $scope.user = null;
                } else {
                    $scope.user = response.data;
                    console.log("✅ User details:", $scope.user);

                }
            }).catch(error => {
                console.log("Lỗi khi load chi tiết:", error);
            });
        };
        $scope.isAuthenticated = function () {
            console.log('Authorities:', $scope.user?.authorities); // Check what authorities array looks like
            if ($scope.user && Array.isArray($scope.user.authorities)) {
                return $scope.user.authorities.some(auth =>
                    auth.authority === 'ROLE_STAFF' || auth.authority === 'ROLE_DIRECTOR'
                );
            }
            return false;
        };

        $scope.updateProductByCategory = function (categoryId) {
            if (categoryId === 0) {
                $scope.loadAllProducts();
            }else{
                $http.get(`${host}/products/category/${categoryId}`).then(response => {
                    $scope.selectedCategoryId = categoryId;
                    $scope.listProductsByCategory = response.data;
                    $scope.listProductsByCategory.forEach(p => {
                        if (p.image) {
                            p.image = `${urlImg}/${p.image}`;
                        }
                    });
                    $scope.pager.setItems(PaginationService.sortBy($scope.listProductsByCategory, $scope.sortColumn, $scope.reverse));
                    console.log('listProductsByCategory:',response.data);
                }).catch(error => {
                    console.log(error);
                })
            }

        }
        $scope.loadAllCategories = function (){
            $http.get(`${host}/categories`).then(response => {
                $scope.listCategories = response.data;
                console.log(response.data);
            }).catch(error => {
                console.log(error);
            });
        }
        $scope.addToCart = function (product) {
            CartService.addToCart(product, 1, $scope.user)
                .then(result => {
                    Swal.fire({
                        icon: 'success',
                        title: product.name + ' đã được thêm vào giỏ hàng!',
                        showConfirmButton: false,
                        timer: 1000
                    });
                    console.log("Kết quả thêm giỏ:", result);
                })
                .catch(error => {
                    console.error("Fail khi thêm giỏ:", error);
                    Swal.fire({
                        icon: 'error',
                        title: 'Thêm mới thất bại!',
                        text: 'Đã có lỗi xảy ra. Vui lòng thử lại.'
                    });
                });
        };
        $scope.pager = PaginationService.createPager(9);

        $scope.sortBy = function (column) {
            if ($scope.sortColumn === column) {
                $scope.reverse = !$scope.reverse; // Đảo thứ tự nếu cùng cột
            } else {
                $scope.sortColumn = column;
                $scope.reverse = false; // Mặc định tăng dần khi đổi cột
            }
            $scope.pager.setItems(PaginationService.sortBy($scope.listProducts, $scope.sortColumn, $scope.reverse));
        };
        $rootScope.$on('searchResults/shop', function(event, items) {
            $timeout(function() {
                $scope.listProducts = items.length ? items : [];
                console.log("Dữ liệu sau khi cập nhật:", $scope.listProducts);
                $scope.pager.setItems(PaginationService.sortBy($scope.listProducts, $scope.sortColumn, $scope.reverse));
            });

        });
        $scope.loadAllCategories();
        $scope.loadAllProducts();
        $scope.loadUserDetails();

        $scope.formatPrice = function(price) {
            return price.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' });
        };

    })
})();
