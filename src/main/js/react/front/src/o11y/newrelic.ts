import { Agent } from '@newrelic/browser-agent/loaders/agent'
import { PageViewEvent } from '@newrelic/browser-agent/features/page_view_event';
import { PageViewTiming } from '@newrelic/browser-agent/features/page_view_timing';
import { Metrics } from '@newrelic/browser-agent/features/metrics';
import { JSErrors } from '@newrelic/browser-agent/features/jserrors';
import { Ajax } from '@newrelic/browser-agent/features/ajax';
import { SessionTrace } from '@newrelic/browser-agent/features/session_trace';
import { Spa } from '@newrelic/browser-agent/features/spa';
import { GenericEvents } from '@newrelic/browser-agent/features/generic_events';
import { Logging } from '@newrelic/browser-agent/features/logging'
import { SessionReplay } from "@newrelic/browser-agent/features/session_replay";

const options = {
    init: {session_replay:{enabled:true,block_selector:'',mask_text_selector:'',sampling_rate:100.0,error_sampling_rate:100.0,mask_all_inputs:true,collect_fonts:true,inline_images:false,inline_stylesheet:true,mask_input_options:{}},distributed_tracing:{enabled:true},privacy:{cookies_enabled:true}, logging: { enabled: true, autoStart: true, loggingMode: 4}},
    info: {beacon:"bam.nr-data.net",errorBeacon:"bam.nr-data.net",licenseKey:"NRJS-d5fc04e71a6af75ae7d",applicationID:"1519106818",sa:1},
    loader_config: {accountID:"3873151",trustKey:"3830171",agentID:"1588910174",licenseKey:"NRJS-d5fc04e71a6af75ae7d",applicationID:"1519106818"},
    features: [SessionReplay, Spa, PageViewEvent, PageViewTiming, Metrics, JSErrors, Ajax, SessionTrace, GenericEvents, SessionReplay, Logging ]
}

const prodOptions = {
    init: {session_replay:{enabled:true,block_selector:'',mask_text_selector:'*',sampling_rate:100.0,error_sampling_rate:100.0,mask_all_inputs:true,collect_fonts:true,inline_images:false,inline_stylesheet:true,mask_input_options:{}},distributed_tracing:{enabled:true},privacy:{cookies_enabled:true}, logging: { enabled: true, autoStart: true, loggingMode: 4} ,ajax:{deny_list:["bam.nr-data.net"]}},
    info: {beacon: 'bam.nr-data.net', errorBeacon: 'bam.nr-data.net', licenseKey: 'NRJS-baf3500e28151acf428', applicationID: '1588977533', sa: 1},
    loader_config: {accountID: '4097184', trustKey: '3830171', agentID: '1588977533', licenseKey: 'NRJS-baf3500e28151acf428', applicationID: '1588977533'},
    features: [SessionReplay, Spa, PageViewEvent, PageViewTiming, Metrics, JSErrors, Ajax, SessionTrace, GenericEvents, SessionReplay, Logging ]
}

const NrAgent = new Agent(process.env.NODE_ENV === 'production' ? prodOptions : options  )
NrAgent.setApplicationVersion(process.env.REACT_APP_VERSION || 'dev')
export default NrAgent
