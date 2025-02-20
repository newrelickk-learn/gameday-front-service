import { BrowserAgent } from '@newrelic/browser-agent/loaders/browser-agent'
import { Logging } from '@newrelic/browser-agent/features/logging'

const options = {
    init: {session_replay:{enabled:true,block_selector:'',mask_text_selector:'',sampling_rate:100.0,error_sampling_rate:100.0,mask_all_inputs:true,collect_fonts:true,inline_images:false,inline_stylesheet:true,mask_input_options:{}},distributed_tracing:{enabled:true},privacy:{cookies_enabled:true}},
    info: {beacon:"bam.nr-data.net",errorBeacon:"bam.nr-data.net",licenseKey:"NRJS-d5fc04e71a6af75ae7d",applicationID:"1519106818",sa:1},
    loader_config: {accountID:"3873151",trustKey:"3830171",agentID:"1588910174",licenseKey:"NRJS-d5fc04e71a6af75ae7d",applicationID:"1519106818"},
    features: [ Logging ]
}

const prodOptions = {
    init: {session_replay:{enabled:true,block_selector:'',mask_text_selector:'*',sampling_rate:100.0,error_sampling_rate:100.0,mask_all_inputs:true,collect_fonts:true,inline_images:false,inline_stylesheet:true,mask_input_options:{}},distributed_tracing:{enabled:true},privacy:{cookies_enabled:true},ajax:{deny_list:["bam.nr-data.net"]}},
    info: {beacon: 'bam.nr-data.net', errorBeacon: 'bam.nr-data.net', licenseKey: 'NRJS-baf3500e28151acf428', applicationID: '1588977533', sa: 1},
    loader_config: {accountID: '4097184', trustKey: '3830171', agentID: '1588977533', licenseKey: 'NRJS-baf3500e28151acf428', applicationID: '1588977533'},
    features: [ Logging ]
}

const NrAgent = new BrowserAgent(process.env.NODE_ENV === 'production' ? prodOptions : options  )
NrAgent.setApplicationVersion(process.env.REACT_APP_VERSION || 'dev')
export default NrAgent
