// parameters

let vendor_name_input_el = document.getElementById("vendor_name");
let cs_model_input_el = document.getElementById("cs_model");
let ocpp_server_address_input_el = document.getElementById("ocpp_server_address");
let accessible_house_power_input_el = document.getElementById("accessible_house_power");
let rpc_server_address_input_el = document.getElementById("rpc_server_address");
let rpc_server_port_input_el = document.getElementById("rpc_server_port");
let embedded_en_meter_id_input_el = document.getElementById("embedded_en_meter_id");
let outer_en_meter_id_input_el = document.getElementById("outer_en_meter_id");
let outer_en_meter_ip_address_input_el = document.getElementById("outer_en_meter_ip_address");
let mode3_controller_port_input_el = document.getElementById("mode3_controller_port");
let connector_id_input_el = document.getElementById("connector_id");
let logger_remote_address_input_el = document.getElementById("logger_remote_address");
let logger_remote_port_input_el = document.getElementById("logger_remote_port");

let ip_addresses_list = [];


let counter = 1;
// HTML SELECTED ELEMENTS
let add_address_btn_el = document.getElementById("add_ip_btn");
let save_params_btn_el = document.getElementById("saves_params_btn");
let configs_form_el = document.querySelector(".configs_form");
let props_group_el = document.querySelector(".props_group");

const gatherParamsInDto = () => {
   const list = [];
    for (let i = 0; i < props_group_el.children[0].children.length; i++) {
        let sn = props_group_el.children[0].children[i].className
        if (sn.startsWith("center ip")) {
            let input_value = props_group_el.children[0].children[i].children[1].value;
            list.push(input_value)
        }
    }
    const paramsDto = {
        vendor_name: vendor_name_input_el.value,
        cs_model: cs_model_input_el.value,
        ocpp_server_address: ocpp_server_address_input_el.value,
        accessible_house_power: accessible_house_power_input_el.value,
        rpc_server_address: rpc_server_address_input_el.value,
        rpc_server_port: rpc_server_port_input_el.value,
        embedded_en_meter_id: embedded_en_meter_id_input_el.value,
        outer_en_meter_id: outer_en_meter_id_input_el.value,
        outer_en_meter_ip_address: outer_en_meter_ip_address_input_el.value,
        mode3_controller_port: mode3_controller_port_input_el.value,
        connector_id: connector_id_input_el.value,
        logger_remote_address: logger_remote_address_input_el.value,
        logger_remote_port: logger_remote_port_input_el.value,
        mode3_controller_addresses: list
    }
    localStorage.setItem("formData", JSON.stringify(paramsDto)); // Store the entire object
    return paramsDto;
}

 //sending data to java
function postParamsToJava() {
    gatherParamsInDto();
    var xhr = new XMLHttpRequest();
    xhr.open("POST", "http://localhost:3060/postDataToJava", false);
    xhr.setRequestHeader('Content-Type', 'text/plain');
    xhr.send(JSON.stringify(gatherParamsInDto()));
}



//todo FOR RENDERING!!!!
console.log(localStorage.getItem("formData"))


const createDiv = () => {
    const new_div_elem = document.createElement("div")
    new_div_elem.className = "sp_between ip" + ++counter;

    const new_input_elem = document.createElement("input");
    new_input_elem.type = "text";
    new_input_elem.id = "mode3_controller_address" + counter;
    new_input_elem.name = "mode3_controller_address" + counter;
    new_input_elem.placeholder = "192.168.3.200";
    new_input_elem.maxLength=13;

    const new_label_elem = document.createElement("label");
    // new_label_elem.id = "ip_input" +counter;
    new_label_elem.htmlFor = "mode3_controller_address" + counter;
    new_label_elem.textContent = "IP адрес:"


    new_div_elem.appendChild(new_label_elem);
    new_div_elem.appendChild(new_input_elem);
    configs_form_el.appendChild(new_div_elem);

    console.log("added" + counter)

}

const handleClick = () => {
    createDiv();
}

add_address_btn_el.addEventListener('click', handleClick)
save_params_btn_el.addEventListener('click', postParamsToJava)


