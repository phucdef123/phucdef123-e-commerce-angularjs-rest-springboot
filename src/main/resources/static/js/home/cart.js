(function () {
    var app = angular.module('cartApp', ['ngCookies']);
    app.controller('cartCtrl', function ($scope, $http, $cookies) {
        $scope.listCartByUsername = [];
        $scope.listCarts = [];
        $scope.cart = {};
        $scope.user = {};
        $scope.address = '';

        var host = "http://localhost:8080/cart";
        var urlImg = "http://localhost:8080/uploads/images";

        $scope.loadUserDetails = function () {
            $http.get('http://localhost:8080/auth/details').then(response => {
                if (response.status !== 200) {
                    console.log("⛔ User chưa đăng nhập hoặc không có dữ liệu.");
                    $scope.user = null;
                } else {
                    $scope.user = response.data;
                    console.log("✅ User details:", $scope.user);

                }
                $scope.loadAllCarts();
                console.log("User details:", $scope.user);
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
        $scope.loadAllCarts = function () {
            console.log($scope.user);
            if ($scope.user) {

                $http.get(`${host}/user/${$scope.user.username}`).then(response => {
                    $scope.listCarts = response.data;
                    $scope.listCarts.forEach(c => {
                        if (c.product.image) {
                            c.product.image = `${urlImg}/${c.product.image}`;
                        }
                    });
                    var cartJson = $cookies.get('cart');
                    if (cartJson) {
                        var cartFromCookie = JSON.parse(cartJson);
                        cartFromCookie.forEach((item) => {
                            item.user = $scope.user;
                        });

                        console.log('Cart from cookie:', cartFromCookie);

                        // Lấy giỏ hàng hiện tại từ server
                        $http.get(`${host}/user/${$scope.user.username}`).then(serverResponse => {
                            var existingCart = serverResponse.data;

                            var updateOrPostPromises = cartFromCookie.map((cookieItem) => {
                                var found = existingCart.find(serverItem => serverItem.product.id === cookieItem.product.id);

                                if (found) {
                                    // Nếu đã tồn tại → update quantity
                                    found.quantity += cookieItem.quantity;
                                    return $http.put(`${host}/${found.id}`, found)
                                        .then(res => {
                                            console.log("✔️ Updated item:", res.data);
                                            return res;
                                        })
                                        .catch(error => {
                                            console.error("❌ Lỗi khi update:", error);
                                            throw error;
                                        });
                                } else {
                                    // Nếu chưa tồn tại → thêm mới
                                    return $http.post(host, cookieItem)
                                        .then(res => {
                                            console.log("🆕 Thêm mới từ cookie thành công:", res.data);
                                            return res;
                                        })
                                        .catch(error => {
                                            console.error("❌ Lỗi khi thêm mới:", error);
                                            throw error;
                                        });
                                }
                            });

                            // Sau khi xử lý xong tất cả thì xóa cookie
                            Promise.all(updateOrPostPromises)
                                .then(() => {
                                    $cookies.remove('cart');
                                    console.log("✅ Đã xóa cookie 'cart' sau khi đồng bộ.");
                                })
                                .catch(error => {
                                    console.error("⚠️ Không xóa cookie do có lỗi khi sync:", error);
                                });

                        }).catch(error => {
                            console.error("❌ Không lấy được giỏ hàng từ server:", error);
                        });
                    }
                    console.log('Giỏ hàng từ server:', response.data);
                }).catch(error => {
                    console.log(error);
                });
            } else {
                var cartJson = $cookies.get('cart'); // cookie với tên 'cart'
                console.log("Cart từ cookie (thô):", cartJson);

                if (!cartJson) {
                    console.warn("👉 Cookie 'cart' không tồn tại hoặc không đọc được!");
                }
                if (cartJson) {
                    $scope.listCarts = JSON.parse(cartJson);
                    console.log('listCarts từ cookie:', $scope.listCarts);
                } else {
                    $scope.listCarts = [];
                }
            }
        };

        $scope.createOrder = function () {
            if ($scope.user) {
                var order = {
                    id: null,
                    user: $scope.user,
                    address: $scope.address,
                    status: 'PENDING',
                };

                $http.post(`${host}/order/${$scope.user.username}`, order)
                    .then(function (response) {
                        var createdOrder = response.data;
                        console.log('✅ Order created:', createdOrder);

                        var orderDetailPromises = $scope.listCarts.map(function (cartItem) {
                            var orderDetail = {
                                id: null,
                                order: { id: createdOrder.id },
                                product: {
                                    id: cartItem.product.id
                                },
                                price: cartItem.product.price,
                                quantity: cartItem.quantity
                            };
                            return $http.post(`${host}/order/orderDetails`, orderDetail, {
                                headers: { 'Content-Type': 'application/json' }
                            }).catch(function(error) {
                                console.log('Order Detail Error:', error);
                                throw error;
                            });
                        });

                        return Promise.all(orderDetailPromises);
                    })
                    .then(function (responses) {
                        console.log('✅ All order details created:', responses);
                        Swal.fire({
                            icon: 'success',
                            title: 'Đặt hàng thành công!',
                            showConfirmButton: false,
                            timer: 1000
                        });
                        $scope.listCarts = [];
                        $cookies.remove('cart');
                    })
                    .catch(function (error) {
                        console.error('❌ Error during order process:', error);
                        Swal.fire({
                            icon: 'error',
                            title: 'Thêm mới thất bại!',
                            text: 'Đã có lỗi xảy ra. Vui lòng thử lại.'
                        });
                    });
            } else {
                window.location.href = 'http://localhost:8080/auth/login/form';
            }
        };


        $scope.update = function () {
            var item = angular.copy($scope.cart);
            $http.put(`${host}/${$scope.cart.id}`, item).then(response => {
                var index = $scope.listCarts.findIndex(item => item.id === $scope.cart.id);
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
        $scope.delete = function (productId) {

            if ($scope.user) {
                // Nếu đã đăng nhập, tìm cart có product.id tương ứng để lấy cart.id
                let cartItem = $scope.listCarts.find(c => c.product.id === productId);
                if (!cartItem || !cartItem.id) {
                    console.error("Không tìm thấy cart item phù hợp!");
                    return;
                }

                let cartId = cartItem.id;
                let url = `${host}/${cartId}`;
                $http.delete(url).then(response => {
                    $scope.listCarts = $scope.listCarts.filter(c => c.id !== cartId);
                    Swal.fire({
                        icon: 'success',
                        title: 'Đã xóa khỏi giỏ hàng!',
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
                });

            } else {
                var cartData = $cookies.get('cart');
                if (cartData) {
                    var cartArray = JSON.parse(cartData);
                    var newCartArray = cartArray.filter(item => item.product.id !== productId);
                    $cookies.put('cart', JSON.stringify(newCartArray), {
                        path: '/',
                        expires: new Date(Date.now() + 3600 * 1000) // giữ lại 1 tiếng
                    });
                    $scope.listCarts = newCartArray;
                    Swal.fire({
                        icon: 'success',
                        title: 'Đã xóa khỏi giỏ hàng!',
                        showConfirmButton: false,
                        timer: 1000
                    });
                }
            }
        };

        $scope.gotoShop = function () {
            window.location.href = '/shop';
        }
        $scope.increase = function (cart) {
            if (!cart.quantity) cart.quantity = 1;
            if (cart.quantity < 99) {
                cart.quantity++;
            }
        };

        $scope.decrease = function (cart) {
            if (!cart.quantity) cart.quantity = 1;
            if (cart.quantity > 1) {
                cart.quantity--;
            }
        };

        $scope.loadUserDetails();
        if ($scope.user == null){
            $scope.loadAllCarts();
        }


        $scope.formatPrice = function(price) {
            return price.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' });
        };

    })
})();
