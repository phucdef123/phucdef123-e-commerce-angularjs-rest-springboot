(function () {
    var app = angular.module('app');

    app.controller('ProductController', function($scope, $rootScope, $http, $routeParams, PaginationService, $timeout) {
        $scope.action = $routeParams.action;
        $scope.items = [];
        $scope.categories = [];
        $scope.form = {};
        $scope.sortColumn = "createDate";
        $scope.reverse = true;// true = giảm dần
        var host = "http://localhost:8080/admin/products";
        console.log($scope.action);

        $scope.delete = function (key) {
            var url = `${host}/${key}`;
            $http.delete(url).then(response => {
                var index = $scope.items.findIndex(item => item.id === key);
                $scope.items.splice(index, 1);
                $scope.reset();
                console.log(response.data);
                Swal.fire({
                    icon: 'success',
                    title: 'Xóa thành công!',
                    showConfirmButton: false,
                    timer: 1000
                });
            }).catch(error => {
                console.log(error);
                Swal.fire({
                    icon: 'error',
                    title: 'Xóa thất bại!',
                    text: 'Đã có lỗi xảy ra. Vui lòng thử lại.'
                });
            })
        }
        $scope.edit = function (key) {
            var url = `${host}/${key}`;
            $http.get(url).then(response => {
                console.log("Dữ liệu từ API:", response.data);
                $scope.form = response.data;
                if ($scope.form.image) {
                    $scope.uploadedImage = `http://localhost:8080/uploads/images/${$scope.form.image}`;
                } else {
                    $scope.uploadedImage = null; // Nếu không có hình ảnh, xóa preview
                }
                console.log(url);
            }).catch(error => {
                console.log(error);
            })
        }
        if ($scope.action === 'edit' || $routeParams.id) {
            $scope.title = "Product Edition";
            console.log($scope.form);
            if ($routeParams.id) {
                console.log("Đang tải sản phẩm với ID:", $routeParams.id);
                $scope.edit($routeParams.id); // Tải dữ liệu sản phẩm dựa trên ID
            } else {
                $scope.form = { available: true }; // Trường hợp tạo mới (không có ID)
            }
            $scope.loadAllCategories = function (){
                $http.get("http://localhost:8080/admin/categories").then(response => {
                    $scope.categories = response.data;
                    console.log(response.data);
                }).catch(error => {
                    console.log(error);
                });
            }
            $scope.create = function () {
                var item = angular.copy($scope.form);
                item.id = null;
                $http.post(host, item).then(function (response) {
                    $scope.items.push(response.data);
                    $scope.reset();
                    console.log(response);
                    Swal.fire({
                        icon: 'success',
                        title: 'Thêm thành công!',
                        showConfirmButton: false,
                        timer: 1000
                    });
                    $timeout(function () {
                        window.location.href = `#!/products/list`;
                    }, 1000);
                }).catch(function (error) {
                    console.log(error);
                    Swal.fire({
                        icon: 'error',
                        title: 'Thêm mới thất bại!',
                        text: 'Đã có lỗi xảy ra. Vui lòng thử lại.'
                    });
                })
            }
            $scope.update = function () {
                var item = angular.copy($scope.form);
                var url = `${host}/${$scope.form.id}`;
                $http.put(url, item).then(response => {
                    var index = $scope.items.findIndex(item => item.id === $scope.form.id);
                    $scope.items[index] = response.data;
                    console.log(response.data);
                    Swal.fire({
                        icon: 'success',
                        title: 'Cập nhật thành công!',
                        showConfirmButton: false,
                        timer: 1000
                    });
                }).catch(error => {
                    console.log(error);
                    Swal.fire({
                        icon: 'error',
                        title: 'Cập nhật thất bại!',
                        text: 'Đã có lỗi xảy ra. Vui lòng thử lại.'
                    });
                })
            }
            $scope.reset = function () {
                $scope.form = {available: true, image: null};
                $scope.uploadedImage = null;
            }
            $scope.uploadFile = function (files) {
                if (files && files[0]) {
                    var reader = new FileReader();
                    reader.onload = function (e) {
                        $scope.$apply(function () {
                            $scope.uploadedImage = e.target.result; // Gán data URL để hiển thị preview
                        });
                    };
                    reader.readAsDataURL(files[0]);
                    var data = new FormData();
                    data.append("file", files[0]);

                    $http.post("http://localhost:8080/admin/upload/images", data, {
                        transformRequest: angular.identity,
                        headers: {'Content-Type': undefined}
                    }).then(function (response) {
                        // $scope.uploadedImage = "http://localhost:8080" + response.data.url;
                        $scope.form.image = response.data.name;

                    }).catch(function (error) {
                        console.log(error);
                    });
                } else {
                    alert("Vui lòng chọn file để tải lên.");
                }
            }

            $scope.loadAllCategories();
        } else if ($scope.action === 'list') {
            $scope.title = "Product List";
            $scope.loadAll = function () {
                $http.get(host).then(response => {
                    $scope.items = response.data;
                    console.log("Sort theo cột:", $scope.sortColumn);
                    console.log("reverse:", $scope.reverse);
                    const sortedItems = PaginationService.sortBy(
                        $scope.items,
                        $scope.sortColumn,
                        $scope.reverse
                    );

                    $scope.pager.setItems(sortedItems);
                }).catch(error => {
                    console.log(error);
                });
            };

            $scope.pager = PaginationService.createPager(10);

            $scope.sortBy = function (column) {
                if ($scope.sortColumn === column) {
                    $scope.reverse = !$scope.reverse; // Đảo thứ tự nếu cùng cột
                } else {
                    $scope.sortColumn = column;
                    $scope.reverse = false; // Mặc định tăng dần khi đổi cột
                }
                $scope.pager.setItems(PaginationService.sortBy($scope.items, $scope.sortColumn, $scope.reverse));
            };

            $scope.loadAll();
            $rootScope.$on('searchResults/products/list', function(event, items) {
                $timeout(function() {
                    $scope.items = items.length ? items : [];
                    console.log("Dữ liệu sau khi cập nhật:", $scope.items);
                    $scope.pager.setItems(PaginationService.sortBy($scope.items, $scope.sortColumn, $scope.reverse));
                });

            });
            // $scope.$on('searchResultsFor/products/list', function(event, items) {
            //     $timeout(function() {
            //         $scope.items = items.length ? items : [];
            //         console.log("Dữ liệu sản phẩm sau khi cập nhật:", $scope.items);
            //         $scope.pager.setItems(PaginationService.sortBy($scope.items, $scope.sortColumn, $scope.reverse));
            //     });
            // });
        }


    });
})();
