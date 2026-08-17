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

const prodOptionsUS = {
    init: {session_replay:{enabled:true,block_selector:'',mask_text_selector:'*',sampling_rate:100.0,error_sampling_rate:100.0,mask_all_inputs:true,collect_fonts:true,inline_images:false,inline_stylesheet:true,mask_input_options:{}},distributed_tracing:{enabled:true},privacy:{cookies_enabled:true}, logging: { enabled: true, autoStart: true, loggingMode: 4} ,ajax:{deny_list:["bam.nr-data.net"]}},
    info: {beacon: 'bam.nr-data.net', errorBeacon: 'bam.nr-data.net', licenseKey: 'NRJS-baf3500e28151acf428', applicationID: '1588977533', sa: 1},
    loader_config: {accountID: '4097184', trustKey: '3830171', agentID: '1588977533', licenseKey: 'NRJS-baf3500e28151acf428', applicationID: '1588977533'},
    features: [SessionReplay, Spa, PageViewEvent, PageViewTiming, Metrics, JSErrors, Ajax, SessionTrace, GenericEvents, SessionReplay, Logging ]
}

const prodOptionsJP = {
    init: {session_replay:{enabled:true,block_selector:'',mask_text_selector:'*',sampling_rate:100.0,error_sampling_rate:100.0,mask_all_inputs:true,collect_fonts:true,inline_images:false,inline_stylesheet:true,mask_input_options:{}},distributed_tracing:{enabled:true},privacy:{cookies_enabled:true}, logging: { enabled: true, autoStart: true, loggingMode: 4} ,ajax:{deny_list:["bam.jp.nr-data.net"]}},
    info: {beacon: 'bam.jp.nr-data.net', errorBeacon: 'bam.jp.nr-data.net', licenseKey: 'NRJS-6a06d4f068122dce31b', applicationID: '43818', sa: 1},
    loader_config: {accountID: '8400635', trustKey: '8263460', agentID: '43818', licenseKey: 'NRJS-6a06d4f068122dce31b', applicationID: '43818'},
    features: [SessionReplay, Spa, PageViewEvent, PageViewTiming, Metrics, JSErrors, Ajax, SessionTrace, GenericEvents, SessionReplay, Logging ]
}

type NrTarget = 'us' | 'jp'

const NR_TARGET_COOKIE = 'nr_target'
const NR_TARGET_MAX_AGE_SECONDS = 60 * 60 * 24 * 365

function isNrTarget(value: string | null): value is NrTarget {
    return value === 'us' || value === 'jp'
}

function readCookie(name: string): string | null {
    const match = document.cookie.match(new RegExp(`(?:^|; )${name}=([^;]*)`))
    return match ? decodeURIComponent(match[1]) : null
}

function resolveNrTarget(): NrTarget {
    const fromQuery = new URLSearchParams(window.location.search).get(NR_TARGET_COOKIE)
    if (isNrTarget(fromQuery)) {
        document.cookie = `${NR_TARGET_COOKIE}=${fromQuery}; path=/; max-age=${NR_TARGET_MAX_AGE_SECONDS}`
        return fromQuery
    }

    const fromCookie = readCookie(NR_TARGET_COOKIE)
    if (isNrTarget(fromCookie)) {
        return fromCookie
    }

    return 'us'
}

const prodOptionsByTarget: Record<NrTarget, typeof prodOptionsUS> = {
    us: prodOptionsUS,
    jp: prodOptionsJP,
}

const NrAgent = new Agent(process.env.NODE_ENV === 'production' ? prodOptionsByTarget[resolveNrTarget()] : options)
NrAgent.setApplicationVersion(process.env.REACT_APP_VERSION || 'dev')
export default NrAgent
