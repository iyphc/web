(function () {
    var h = React.createElement;
    var root = ReactDOM.createRoot(document.getElementById("root"));
    var ctx = window.APP_CONTEXT || "";

    function money(value) {
        return value == null ? "" : new Intl.NumberFormat("ru-RU").format(Number(value)) + " ₽";
    }

    function dateTime(value) {
        return value ? String(value).replace("T", " ").slice(0, 16) : "";
    }

    function api(path, options) {
        return fetch(ctx + path, options || {})
            .then(function (response) {
                return response.json().then(function (json) {
                    if (!response.ok) {
                        throw new Error(json.message || "Ошибка запроса");
                    }
                    return json;
                });
            });
    }

    function post(path, data) {
        return api(path, {
            method: "POST",
            headers: {"Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"},
            body: new URLSearchParams(data)
        });
    }

    function formValues(event) {
        var values = {};
        new FormData(event.currentTarget).forEach(function (value, key) {
            values[key] = value;
        });
        return values;
    }

    function Nav() {
        var path = location.pathname.replace(ctx, "") || "/";
        var items = [
            ["/", "Сводка"],
            ["/cars", "Автомобили"],
            ["/brands", "Марки"],
            ["/clients", "Клиенты"],
            ["/orders", "Заказы"],
            ["/test-drives", "Тест-драйвы"]
        ];
        return h("header", {className: "topbar"},
            h("a", {className: "brand-mark", href: ctx + "/"}, "Автосалон"),
            h("nav", {className: "nav"}, items.map(function (item) {
                var active = item[0] === "/" ? path === "/" : path.indexOf(item[0]) === 0;
                return h("a", {key: item[0], className: active ? "active" : "", href: ctx + item[0]}, item[1]);
            }))
        );
    }

    function Message(props) {
        if (!props.text) {
            return null;
        }
        return h("div", {className: "message" + (props.error ? " error" : "")}, props.text);
    }

    function Field(props) {
        var inputProps = Object.assign({}, props);
        delete inputProps.label;
        delete inputProps.wide;
        return h("label", {className: props.wide ? "wide" : ""},
            props.label,
            props.type === "textarea" ? h("textarea", inputProps) : h("input", inputProps)
        );
    }

    function SelectField(props) {
        return h("label", {className: props.wide ? "wide" : ""},
            props.label,
            h("select", {name: props.name, defaultValue: props.defaultValue || ""},
                h("option", {value: ""}, props.empty || "Не выбрано"),
                props.items.map(function (item) {
                    return h("option", {key: item.value, value: item.value}, item.label);
                })
            )
        );
    }

    function CheckField(props) {
        return h("label", {className: "checkline"},
            h("input", {type: "checkbox", name: props.name, defaultChecked: props.defaultChecked}),
            props.label
        );
    }

    function DataTable(props) {
        return h("div", {className: "table-wrap"},
            h("table", null,
                h("thead", null, h("tr", null, props.columns.map(function (col) {
                    return h("th", {key: col.key}, col.title);
                }))),
                h("tbody", null,
                    props.rows.length ? props.rows.map(function (row) {
                        return h("tr", {key: row.id}, props.columns.map(function (col) {
                            return h("td", {key: col.key}, col.render ? col.render(row) : row[col.key]);
                        }));
                    }) : h("tr", null, h("td", {colSpan: props.columns.length, className: "empty"}, "Нет данных"))
                )
            )
        );
    }

    function App() {
        var path = location.pathname.replace(ctx, "") || "/";
        var page = path.split("/")[1] || "dashboard";
        var entityId = window.APP_ENTITY_ID && window.APP_ENTITY_ID !== "" ? Number(window.APP_ENTITY_ID) : null;
        var state = React.useState({loading: true, message: "", error: ""});
        var model = state[0];
        var setModel = state[1];

        function patch(next) {
            setModel(function (current) {
                return Object.assign({}, current, next);
            });
        }

        function refresh() {
            patch({loading: true});
            Promise.all([
                api("/api/dashboard"),
                api("/api/brands"),
                api("/api/cars"),
                api("/api/clients"),
                api("/api/orders"),
                api("/api/test-drives")
            ]).then(function (values) {
                patch({
                    loading: false,
                    dashboard: values[0],
                    brands: values[1],
                    cars: values[2],
                    clients: values[3],
                    orders: values[4],
                    drives: values[5],
                    selectedId: entityId || null
                });
            }).catch(function (err) {
                patch({loading: false, error: err.message});
            });
        }

        React.useEffect(refresh, []);

        function submit(path, values, success) {
            post(path, values).then(function (json) {
                patch({message: json.message || success, error: ""});
                refresh();
            }).catch(function (err) {
                patch({error: err.message, message: ""});
            });
        }

        function remove(path) {
            if (!window.confirm("Удалить запись?")) {
                return;
            }
            submit(path, {}, "Запись удалена");
        }

        var content = model.loading
            ? h("div", {className: "panel"}, "Загрузка...")
            : renderPage(page, model, patch, submit, remove);

        return h("div", {className: "app-shell"},
            h(Nav),
            h("main", {className: "page"},
                h(Message, {text: model.message}),
                h(Message, {text: model.error, error: true}),
                content
            )
        );
    }

    function renderPage(page, model, patch, submit, remove) {
        if (page === "cars") {
            return h(CarsPage, {model: model, patch: patch, submit: submit, remove: remove});
        }
        if (page === "brands") {
            return h(BrandsPage, {model: model, submit: submit, remove: remove});
        }
        if (page === "clients") {
            return h(ClientsPage, {model: model, patch: patch, submit: submit, remove: remove});
        }
        if (page === "orders") {
            return h(OrdersPage, {model: model, patch: patch, submit: submit, remove: remove});
        }
        if (page === "test-drives") {
            return h(TestDrivesPage, {model: model, submit: submit, remove: remove});
        }
        return h(DashboardPage, {model: model});
    }

    function DashboardPage(props) {
        var d = props.model.dashboard || {};
        var stats = [
            ["Автомобили", d.cars],
            ["Марки", d.brands],
            ["Клиенты", d.clients],
            ["Заказы", d.orders],
            ["Тест-драйвы", d.testDrives]
        ];
        return h(React.Fragment, null,
            h("div", {className: "page-head"},
                h("div", null, h("h1", null, "Рабочая панель"), h("div", {className: "muted"}, "Учет автомобилей, клиентов, заказов и тест-драйвов"))
            ),
            h("section", {className: "grid stats"}, stats.map(function (stat) {
                return h("a", {className: "stat", key: stat[0], href: ctx + linkForStat(stat[0])},
                    h("span", {className: "stat-value"}, stat[1] || 0),
                    h("span", null, stat[0])
                );
            }))
        );
    }

    function linkForStat(title) {
        return title === "Автомобили" ? "/cars" : title === "Марки" ? "/brands" :
            title === "Клиенты" ? "/clients" : title === "Заказы" ? "/orders" : "/test-drives";
    }

    function brandOptions(model) {
        return (model.brands || []).map(function (brand) {
            return {value: brand.id, label: brand.brandName + " / " + brand.manufacturerName};
        });
    }

    function carOptions(model) {
        return (model.cars || []).map(function (car) {
            return {value: car.id, label: car.brand.brandName + " " + car.registrationNumber};
        });
    }

    function clientOptions(model) {
        return (model.clients || []).map(function (client) {
            return {value: client.id, label: client.fullName + " / " + client.phone};
        });
    }

    function CarsPage(props) {
        var model = props.model;
        var selected = (model.cars || []).filter(function (car) { return car.id === model.selectedId; })[0];
        return h(React.Fragment, null,
            h("div", {className: "page-head"},
                h("div", null, h("h1", null, "Автомобили"), h("div", {className: "muted"}, "Поиск, карточки, добавление и редактирование"))
            ),
            h("div", {className: "workspace"},
                h("section", {className: "panel"},
                    h("div", {className: "toolbar"},
                        h("input", {placeholder: "Госномер", onChange: function (e) { filterCars(props, {registrationNumber: e.target.value}); }}),
                        h("input", {placeholder: "Цвет", onChange: function (e) { filterCars(props, {color: e.target.value}); }})
                    ),
                    h(DataTable, {rows: model.cars || [], columns: [
                        {key: "brand", title: "Марка", render: function (car) { return car.brand.brandName; }},
                        {key: "registrationNumber", title: "Госномер"},
                        {key: "transmissionType", title: "КПП"},
                        {key: "mileageKm", title: "Пробег"},
                        {key: "price", title: "Цена", render: function (car) { return money(car.price); }},
                        {key: "actions", title: "", render: function (car) {
                            return h("div", {className: "row-actions"},
                                h("button", {onClick: function () { props.patch({selectedId: car.id}); }}, "Открыть"),
                                h("button", {className: "danger", onClick: function () { props.remove("/api/cars/" + car.id + "/delete"); }}, "Удалить")
                            );
                        }}
                    ]})
                ),
                h("aside", {className: "panel"},
                    h("h2", null, selected ? "Карточка автомобиля" : "Новый автомобиль"),
                    selected ? h(CarDetails, {car: selected}) : null,
                    h(CarForm, {model: model, car: selected, submit: props.submit})
                )
            )
        );
    }

    function filterCars(props, query) {
        var search = new URLSearchParams(query).toString();
        api("/api/cars?" + search).then(function (cars) {
            props.patch({cars: cars});
        });
    }

    function CarDetails(props) {
        var car = props.car;
        return h("dl", {className: "detail-list"},
            h("dt", null, "Марка"), h("dd", null, car.brand.brandName),
            h("dt", null, "Двигатель"), h("dd", null, car.engineVolumeL + " л, " + car.enginePowerHp + " л.с."),
            h("dt", null, "Топливо"), h("dd", null, car.requiredFuel),
            h("dt", null, "Цвет"), h("dd", null, car.color || ""),
            h("dt", null, "ТО"), h("dd", null, car.lastServiceDate || "")
        );
    }

    function CarForm(props) {
        var car = props.car || {};
        return h("form", {className: "field-grid", onSubmit: function (e) {
                e.preventDefault();
                props.submit(car.id ? "/api/cars/" + car.id : "/api/cars", formValues(e), "Автомобиль сохранен");
            }},
            h(SelectField, {name: "brandId", label: "Марка", items: brandOptions(props.model), defaultValue: car.brand && car.brand.id}),
            h(Field, {name: "registrationNumber", label: "Госномер", defaultValue: car.registrationNumber || ""}),
            h(Field, {name: "engineVolumeL", label: "Объем двигателя", defaultValue: car.engineVolumeL || ""}),
            h(Field, {name: "enginePowerHp", label: "Мощность", defaultValue: car.enginePowerHp || ""}),
            h(Field, {name: "fuelConsumptionL100km", label: "Расход", defaultValue: car.fuelConsumptionL100km || ""}),
            h(SelectField, {name: "transmissionType", label: "КПП", items: ["AT", "MT", "CVT", "AMT"].map(function (x) { return {value: x, label: x}; }), defaultValue: car.transmissionType}),
            h(Field, {name: "requiredFuel", label: "Топливо", defaultValue: car.requiredFuel || "AI-95"}),
            h(Field, {name: "price", label: "Цена", defaultValue: car.price || ""}),
            h(Field, {name: "mileageKm", label: "Пробег", defaultValue: car.mileageKm || "0"}),
            h(Field, {name: "color", label: "Цвет", defaultValue: car.color || ""}),
            h(Field, {name: "doorsCount", label: "Двери", defaultValue: car.doorsCount || "4"}),
            h(Field, {name: "seatsCount", label: "Места", defaultValue: car.seatsCount || "5"}),
            h(Field, {name: "trunkCapacityL", label: "Багажник", defaultValue: car.trunkCapacityL || ""}),
            h(Field, {name: "interiorTrim", label: "Отделка", defaultValue: car.interiorTrim || ""}),
            h(Field, {name: "lastServiceDate", label: "Дата ТО", type: "date", defaultValue: car.lastServiceDate || ""}),
            h("div", null,
                h(CheckField, {name: "hasCruiseControl", label: "Круиз-контроль", defaultChecked: car.hasCruiseControl}),
                h(CheckField, {name: "hasAirConditioner", label: "Кондиционер", defaultChecked: car.hasAirConditioner}),
                h(CheckField, {name: "hasRadio", label: "Радио", defaultChecked: car.hasRadio}),
                h(CheckField, {name: "hasVideoSystem", label: "Видео", defaultChecked: car.hasVideoSystem}),
                h(CheckField, {name: "hasGps", label: "GPS", defaultChecked: car.hasGps})
            ),
            h("button", {className: "primary", type: "submit"}, car.id ? "Сохранить" : "Добавить")
        );
    }

    function BrandsPage(props) {
        return h(React.Fragment, null,
            h("div", {className: "page-head"}, h("div", null, h("h1", null, "Марки автомобилей"))),
            h("div", {className: "workspace"},
                h("section", {className: "panel"}, h(DataTable, {rows: props.model.brands || [], columns: [
                    {key: "brandName", title: "Марка"},
                    {key: "manufacturerName", title: "Производитель"},
                    {key: "actions", title: "", render: function (brand) {
                        return h("button", {className: "danger", onClick: function () { props.remove("/api/brands/" + brand.id + "/delete"); }}, "Удалить");
                    }}
                ]})),
                h("aside", {className: "panel"}, h("h2", null, "Новая марка"), h(SimpleForm, {
                    fields: [["brandName", "Марка"], ["manufacturerName", "Производитель"]],
                    submit: function (values) { props.submit("/api/brands", values, "Марка сохранена"); }
                }))
            )
        );
    }

    function ClientsPage(props) {
        var model = props.model;
        var selected = (model.clients || []).filter(function (client) { return client.id === model.selectedId; })[0];
        return h(React.Fragment, null,
            h("div", {className: "page-head"}, h("div", null, h("h1", null, "Клиенты"))),
            h("div", {className: "workspace"},
                h("section", {className: "panel"},
                    h("div", {className: "toolbar"}, h("input", {placeholder: "ФИО, телефон или e-mail", onChange: function (e) {
                        api("/api/clients?q=" + encodeURIComponent(e.target.value)).then(function (clients) { props.patch({clients: clients}); });
                    }})),
                    h(DataTable, {rows: model.clients || [], columns: [
                        {key: "fullName", title: "ФИО"},
                        {key: "phone", title: "Телефон"},
                        {key: "email", title: "E-mail"},
                        {key: "actions", title: "", render: function (client) {
                            return h("div", {className: "row-actions"},
                                h("button", {onClick: function () { props.patch({selectedId: client.id}); }}, "Открыть"),
                                h("button", {className: "danger", onClick: function () { props.remove("/api/clients/" + client.id + "/delete"); }}, "Удалить")
                            );
                        }}
                    ]})
                ),
                h("aside", {className: "panel"},
                    h("h2", null, selected ? "Карточка клиента" : "Новый клиент"),
                    selected ? h("dl", {className: "detail-list"},
                        h("dt", null, "Адрес"), h("dd", null, selected.address || ""),
                        h("dt", null, "Телефон"), h("dd", null, selected.phone),
                        h("dt", null, "E-mail"), h("dd", null, selected.email || "")
                    ) : null,
                    h(ClientForm, {client: selected, submit: props.submit})
                )
            )
        );
    }

    function ClientForm(props) {
        var client = props.client || {};
        return h("form", {className: "field-grid", onSubmit: function (e) {
                e.preventDefault();
                props.submit(client.id ? "/api/clients/" + client.id : "/api/clients", formValues(e), "Клиент сохранен");
            }},
            h(Field, {name: "fullName", label: "ФИО", wide: true, defaultValue: client.fullName || ""}),
            h(Field, {name: "phone", label: "Телефон", defaultValue: client.phone || ""}),
            h(Field, {name: "email", label: "E-mail", defaultValue: client.email || ""}),
            h(Field, {name: "address", label: "Адрес", wide: true, defaultValue: client.address || ""}),
            h("button", {className: "primary", type: "submit"}, client.id ? "Сохранить" : "Добавить")
        );
    }

    function OrdersPage(props) {
        return h(React.Fragment, null,
            h("div", {className: "page-head"}, h("div", null, h("h1", null, "Заказы"))),
            h("div", {className: "workspace"},
                h("section", {className: "panel"}, h(DataTable, {rows: props.model.orders || [], columns: [
                    {key: "orderedAt", title: "Дата", render: function (order) { return dateTime(order.orderedAt); }},
                    {key: "client", title: "Клиент", render: function (order) { return order.client.fullName; }},
                    {key: "car", title: "Автомобиль", render: function (order) { return order.car ? order.car.brand.brandName + " " + order.car.registrationNumber : "По требованиям"; }},
                    {key: "status", title: "Статус", render: function (order) {
                        return h("select", {defaultValue: order.status, onChange: function (e) {
                            props.submit("/api/orders/" + order.id + "/status", {status: e.target.value}, "Статус обновлен");
                        }}, ["IN_PROGRESS", "WAITING_SUPPLY", "IN_SHOWROOM", "TEST_DRIVE", "COMPLETED"].map(function (s) {
                            return h("option", {key: s, value: s}, s);
                        }));
                    }},
                    {key: "actions", title: "", render: function (order) {
                        return h("button", {className: "danger", onClick: function () { props.remove("/api/orders/" + order.id + "/delete"); }}, "Удалить");
                    }}
                ]})),
                h("aside", {className: "panel"}, h("h2", null, "Новый заказ"), h(OrderForm, {model: props.model, submit: props.submit}))
            )
        );
    }

    function OrderForm(props) {
        return h("form", {className: "field-grid", onSubmit: function (e) {
                e.preventDefault();
                props.submit("/api/orders", formValues(e), "Заказ создан");
            }},
            h(SelectField, {name: "clientId", label: "Клиент", items: clientOptions(props.model)}),
            h(SelectField, {name: "carId", label: "Автомобиль", items: carOptions(props.model), empty: "По требованиям"}),
            h(CheckField, {name: "needTestDrive", label: "Нужен тест-драйв"}),
            h(SelectField, {name: "desiredBrandId", label: "Желаемая марка", items: brandOptions(props.model)}),
            h(SelectField, {name: "desiredTransmissionType", label: "Желаемая КПП", items: ["AT", "MT", "CVT", "AMT"].map(function (x) { return {value: x, label: x}; })}),
            h(Field, {name: "desiredPriceMax", label: "Макс. цена"}),
            h(Field, {name: "desiredColor", label: "Цвет"}),
            h(Field, {name: "desiredEnginePowerMin", label: "Мин. мощность"}),
            h(Field, {name: "commentText", label: "Комментарий", type: "textarea", wide: true}),
            h("button", {className: "primary", type: "submit"}, "Создать")
        );
    }

    function TestDrivesPage(props) {
        return h(React.Fragment, null,
            h("div", {className: "page-head"}, h("div", null, h("h1", null, "Тест-драйвы"))),
            h("div", {className: "workspace"},
                h("section", {className: "panel"}, h(DataTable, {rows: props.model.drives || [], columns: [
                    {key: "testDriveAt", title: "Дата", render: function (drive) { return dateTime(drive.testDriveAt); }},
                    {key: "client", title: "Клиент", render: function (drive) { return drive.client.fullName; }},
                    {key: "car", title: "Автомобиль", render: function (drive) { return drive.car.brand.brandName + " " + drive.car.registrationNumber; }},
                    {key: "notes", title: "Заметки"},
                    {key: "actions", title: "", render: function (drive) {
                        return h("button", {className: "danger", onClick: function () { props.remove("/api/test-drives/" + drive.id + "/delete"); }}, "Удалить");
                    }}
                ]})),
                h("aside", {className: "panel"}, h("h2", null, "Фиксация тест-драйва"), h(TestDriveForm, {model: props.model, submit: props.submit}))
            )
        );
    }

    function TestDriveForm(props) {
        return h("form", {className: "field-grid", onSubmit: function (e) {
                e.preventDefault();
                props.submit("/api/test-drives", formValues(e), "Тест-драйв сохранен");
            }},
            h(SelectField, {name: "clientId", label: "Клиент", items: clientOptions(props.model)}),
            h(SelectField, {name: "carId", label: "Автомобиль", items: carOptions(props.model)}),
            h(Field, {name: "testDriveAt", label: "Дата и время", type: "datetime-local"}),
            h(Field, {name: "notes", label: "Заметки", type: "textarea", wide: true}),
            h("button", {className: "primary", type: "submit"}, "Зафиксировать")
        );
    }

    function SimpleForm(props) {
        return h("form", {className: "field-grid", onSubmit: function (e) {
                e.preventDefault();
                props.submit(formValues(e));
                e.currentTarget.reset();
            }},
            props.fields.map(function (field) {
                return h(Field, {key: field[0], name: field[0], label: field[1], wide: true});
            }),
            h("button", {className: "primary", type: "submit"}, "Добавить")
        );
    }

    root.render(h(App));
})();
