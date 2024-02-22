import { BrowserAgent } from '@newrelic/browser-agent/loaders/browser-agent'

const options = {
    init: {distributed_tracing:{enabled:true},privacy:{cookies_enabled:true}},
    info: {beacon:"bam.nr-data.net",errorBeacon:"bam.nr-data.net",licenseKey:"NRJS-d5fc04e71a6af75ae7d",applicationID:"1519106818",sa:1},
    loader_config: {accountID:"3873151",trustKey:"3830171",agentID:"1588910174",licenseKey:"NRJS-d5fc04e71a6af75ae7d",applicationID:"1519106818"},
}

const prodOptions = {
    init: {session_replay:{enabled:true,block_selector:'',mask_text_selector:'*',sampling_rate:100.0,error_sampling_rate:100.0,mask_all_inputs:true,collect_fonts:true,inline_images:false,inline_stylesheet:true,mask_input_options:{}},distributed_tracing:{enabled:true},privacy:{cookies_enabled:true},ajax:{deny_list:["bam.nr-data.net"]}},
    info: {beacon:"bam.nr-data.net",errorBeacon:"bam.nr-data.net",licenseKey:"NRJS-c74178fae190d128df7",applicationID:"1588920133",sa:1},
    loader_config: {accountID:"4365310",trustKey:"3830171",agentID:"1588920133",licenseKey:"NRJS-c74178fae190d128df7",applicationID:"1588920133"},
}

export default new BrowserAgent(process.env.NODE_ENV === 'production' ? prodOptions : options)
