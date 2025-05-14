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



////////////////////////////////////////////////////////////////////////////////////////

// // mapping gotten data to object so as, to give out from it values to the dom-components
// function parseReceivedDataToDto(arrayWithReceivedData) {
//     const receivedParamDto = {
//         evse_u: arrayWithReceivedData["evse_U"],
//         evse_i: arrayWithReceivedData["evse_I"],
//         evse_maxU: arrayWithReceivedData["evse_maxU"],
//         evse_maxI: arrayWithReceivedData["evse_maxI"],
//         evse_maxP: arrayWithReceivedData["evse_maxP"],
//         cp_u: arrayWithReceivedData["cp_U"],
//         cp_freq: arrayWithReceivedData["cp_Freq"],
//         cp_dutyCycle: arrayWithReceivedData["cp_DutyCicle"],
//         dt_message: arrayWithReceivedData["dt_message"],
//         stage: arrayWithReceivedData["stage"],
//         contactorRequest: arrayWithReceivedData["contactorRequest"]
//     }
//     console.log(receivedParamDto)
//     return receivedParamDto;
// }
//
// const giveOutDataFromReceivedDtoToDomComponents = (dto) => {
//     evse_U_Field.value = dto.evse_u;
//     evse_I_Field.value = dto.evse_i;
//     evse_maxU_Field.value = dto.evse_maxU;
//     evse_maxI_Field.value = dto.evse_maxI;
//     evse_maxP_Field.value = dto.evse_maxP;
//     stage_Field.value = dto.stage;
//     contactorStatus_Indicator.value = dto.contactorStatus;
// }
//
// const gatherDomComponentsDataToSentParamObj = () => {
//     const sentParamDto = {
//         startStop: startStop_Btn.value,
//         ev_u: ev_u_Field.value,
//         ev_i: ev_i_Field.value,
//         ev_maxU: ev_maxU_Field.value,
//         ev_maxI: ev_maxI_Field.value,
//         ev_maxP: ev_maxP_Field.value,
//         timeCharge: timeCharge_Field.value,
//         cp_on: cp_Btn.value,
//         err_code: err_code_Field.value,
//         ready: ready_Field.value,
//         soc: soc_Field.value,
//         contactorStatus: contactorsStatus,
//         protocol: protocol_Field.value
//     }
//     console.log(protocol_Field.value)
//     return sentParamDto;
// }
//
// function getDataXMLHttpRequest() {
//     var xhr = new XMLHttpRequest();
//     xhr.open('GET', 'http://127.0.0.1:3060/getDataFromJava', false);
//     xhr.setRequestHeader('Content-Type', 'text/plain');
//     xhr.send();
//     const jsonString = xhr.response;
//     const jsonObj = new Function('return ' + jsonString)(); // string to json obj
//     let receivedDto;
//
//     try {
//         if ((jsonObj["sentData"] !== undefined && jsonObj["sentData"] !== null) ||
//             (jsonObj["receivedData"] !== undefined && jsonObj["receivedData"] !== null)) {
//             chart1.data.labels.push(new Date());// increases our x-axis
//         }
//         // building graphs with outgoing params  __________________________
//         if (jsonObj["sentData"] !== null && jsonObj["sentData"] !== undefined) {
//             buildGraphBaseOnCheckBoxesStates("ev_u", dataSetEV_U, jsonObj["sentData"], checkBoxEv_U);
//             buildGraphBaseOnCheckBoxesStates("ev_i", dataSetEV_I, jsonObj["sentData"], checkBoxEv_I);
//
//         }
//         // building graphs with incoming params  __________________________
//         if (jsonObj["receivedData"] !== null && jsonObj["receivedData"] !== undefined) {
//             receivedDto = parseReceivedDataToDto(jsonObj["receivedData"]);
//             giveOutDataFromReceivedDtoToDomComponents(receivedDto)
//             buildGraphBaseOnCheckBoxesStates("evse_U", dataSetEVSE_U, jsonObj["receivedData"], checkBoxEVSE_U);
//             buildGraphBaseOnCheckBoxesStates("evse_I", dataSetEVSE_I, jsonObj["receivedData"], checkBoxEVSE_I);
//             buildGraphBaseOnCheckBoxesStates("cp_U", dataSetCP_U, jsonObj["receivedData"], checkBoxCP_U);
//             buildGraphBaseOnCheckBoxesStates("cp_Freq", dataSetCP_Freq, jsonObj["receivedData"], checkBoxCPFreq);
//             buildGraphBaseOnCheckBoxesStates("cp_DutyCicle", dataSetCP_DutyCycle, jsonObj["receivedData"], checkBoxCPDutyCycle);
//             buildGraphBaseOnCheckBoxesStates("dt_message", dataSetDt_message, jsonObj["receivedData"], checkBoxDtMsg);
//             buildGraphBaseOnCheckBoxesStates("stage", dataSetStage, jsonObj["receivedData"], checkBoxStage);
//             let currentStage = Number(jsonObj["receivedData"]["stage"]);
//             renderIndicators(currentStage);
//         }
//     } catch (e) {
//         console.log('Empty object')
//     }
//
// }
//
// //sending data to java
// function postChosenParamsToJava() {
//     var xhr = new XMLHttpRequest();
//     xhr.open("POST", "http://127.0.0.1:3060/postDataToJava", false);
//     xhr.setRequestHeader('Content-Type', 'text/plain');
//     xhr.send(JSON.stringify(gatherDomComponentsDataToSentParamObj()));
// }
//
//
//
// function startCharge() {
//     if (startStop_Btn.value === "0") {
//         startStop_Btn.value = "1";
//         console.log("start btn pressed: " + startStop_Btn.value)
//     } else {
//         startStop_Btn.value = "0";
//         console.log("start btn unpressed: " + startStop_Btn.value);
//     }
// }
//
// function toggleCp() {
//     if (cp_Btn.value === "0") {
//         cp_Btn.value = "1";
//         console.log("cp on: " + cp_Btn.value)
//     } else {
//         cp_Btn.value = "0";
//         console.log("cp off: " + cp_Btn.value);
//     }
// }
//
// let contactorsStatus = "0";
// function dischargeContactors() {
//     if (contactorsStatus === "0") {
//         contactorsStatus = "1";
//         console.log("contactors locked: " + contactorsStatus)
//     }
// }
//
// function resetContactors() {
//     if (contactorsStatus === "1") {
//         contactorsStatus = "0";
//         console.log("contactors unlocked: " + contactorsStatus)
//
//     }
// }
//
// function renderIndicators(stageState) {
//     if (stageState === 1) {
//         stage_Indicator1.style.color = "green";
//         stage_Indicator2.style.color = "#939191";
//         stage_Indicator3.style.color = "#939191";
//         stage_Indicator4.style.color = "#939191";
//     } else if (stageState === 2) {
//         stage_Indicator1.style.color = "green";
//         stage_Indicator2.style.color = "green";
//         stage_Indicator3.style.color = "#939191";
//         stage_Indicator4.style.color = "#939191";
//     } else if (stageState === 3) {
//         stage_Indicator1.style.color = "green";
//         stage_Indicator2.style.color = "green";
//         stage_Indicator3.style.color = "green";
//         stage_Indicator4.style.color = "#939191";
//     } else if (stageState === 4) {
//         stage_Indicator1.style.color = "green";
//         stage_Indicator2.style.color = "green";
//         stage_Indicator3.style.color = "green";
//         stage_Indicator4.style.color = "green";
//     } else {
//         stage_Indicator1.style.color = "#939191";
//         stage_Indicator2.style.color = "#939191";
//         stage_Indicator3.style.color = "#939191";
//         stage_Indicator4.style.color = "#939191";
//     }
// }
//
// // time delay  ! NOTE it's unused however is very need so it's time delay in JS
// const sleep = ms => new Promise(resolve => setTimeout(resolve, ms));
// // await sleep(500);
//
//
// // GRAPH BUILDERS
// async function buildGraphBaseOnCheckBoxesStates(paramName, dataSet, array, checkBoxState) {
//     if (checkBoxState.checked === true) {
//         buildGraph(paramName, dataSet, array)
//     }
// }
//
// async function buildGraph(paramName, dataSet, array) {
//     let param;
//     if (array !== null && array !== undefined) {
//         param = array[paramName];
//         let number = await transform(param);
//         if (!isNaN(number)) {
//             dataSet.data.push(number);
//         }
//         chart1.update();
//     }
// }
//
//
// async function transform(data) {
//     let a = Number(data);
//     if (!isNaN(a) || a !== undefined)
//         return a
// }
//
// // Chart js , everything for assembling graph
// let dataSetEV_U = createDataSet("EV_U", "#a8acfd", "#a8acfd");
// let dataSetEVSE_U = createDataSet("EVSE_U", "#004891", "#004891");
// let dataSetEV_I = createDataSet("EV_I", "#6fff6f", "#6fff6f");
// let dataSetEVSE_I = createDataSet("EVSE_I", "#008000", "#008000");
// let dataSetCP_U = createDataSet("CP_U", "#ff8000", "#ff8000");
// let dataSetCP_Freq = createDataSet("CP_Freq", "#ffb76f", "#ffb76f");
// let dataSetCP_DutyCycle = createDataSet("CP_DutyCycle", "#b05800", "#b05800");
// let dataSetDt_message = createDataSet("Dt_message", "#ff00ff", "#ff00ff");
// let dataSetStage = createDataSet("Stage", "#ff0000", "#ff0000");
//
//
// function createDataSet(label, backgroundColor, borderColor) {
//     return {
//         label: label,
//         lineTension: 0,
//         backgroundColor: backgroundColor,
//         borderColor: borderColor,
//         fill: false,
//         data: [],
//         borderWidth: 1
//     }
// }
//
//
// const config = {
//     type: 'line',
//     data: {
//         labels: [],
//         datasets: [dataSetEV_U, dataSetEV_I, dataSetEVSE_U, dataSetEVSE_I, dataSetCP_U,
//             dataSetCP_Freq, dataSetCP_DutyCycle, dataSetStage, dataSetDt_message]
//     },
//     options: {
//         scales: {
//             xAxes: [{
//                 type: 'time',
//             },],
//         },
//         pan: {
//             enabled: true,
//             mode: 'xy',
//         },
//         zoom: {
//             enabled: true,
//             mode: 'xy', // or 'x' for "drag" version
//         },
//     },
// };
//
// var chart1;
// window.onload = function () {
//     chart1 = new Chart(document.getElementById('chart'), config);
// };
//
//
// // run methods
// setInterval(postChosenParamsToJava, 50);
// setInterval(getDataXMLHttpRequest, 50);
//
