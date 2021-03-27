package org.xaplus.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xaplus.engine.events.xaplus.*;

/**
 * @since 1.0.0
 * @author Kirill Byvshev (k@byv.sh)
 */
@RestController
class XAPlusRestController {
    static private final Logger logger = LoggerFactory.getLogger(XAPlusRestController.class);

    private final XAPlusDispatcher dispatcher;

    XAPlusRestController(XAPlusDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @RequestMapping("xaplus/prepare")
    boolean prepare(@RequestParam("xid") String xidString) {
        try {
            XAPlusXid xid = new XAPlusXid(xidString);
            if (logger.isDebugEnabled()) {
                logger.debug("Got prepare request for xid={} from superior server", xid);
            }
            dispatcher.dispatch(new XAPlusRemoteSuperiorOrderToPrepareEvent(xid));
            return true;
        } catch (IllegalArgumentException iae) {
            return false;
        } catch (InterruptedException ie) {
            return false;
        }
    }

    @RequestMapping("xaplus/cancelled")
    boolean cancelled(@RequestParam("xid") String xidString) {
        try {
            XAPlusXid xid = new XAPlusXid(xidString);
            if (logger.isDebugEnabled()) {
                logger.debug("Got cancelled status for xid={} from subordinate server", xid);
            }
            dispatcher.dispatch(new XAPlusRemoteSubordinateCancelledEvent(xid));
            return true;
        } catch (IllegalArgumentException iae) {
            return false;
        } catch (InterruptedException ie) {
            return false;
        }
    }

    @RequestMapping("xaplus/readied")
    boolean ready(@RequestParam("xid") String xidString) {
        try {
            XAPlusXid xid = new XAPlusXid(xidString);
            if (logger.isDebugEnabled()) {
                logger.debug("Got readied status for xid={} from subordinate server", xid);
            }
            dispatcher.dispatch(new XAPlusRemoteSubordinateReadiedEvent(xid));
            return true;
        } catch (IllegalArgumentException iae) {
            return false;
        } catch (InterruptedException ie) {
            return false;
        }
    }

    @RequestMapping("xaplus/commit")
    boolean commit(@RequestParam("xid") String xidString) {
        try {
            XAPlusXid xid = new XAPlusXid(xidString);
            if (logger.isDebugEnabled()) {
                logger.debug("Got commit request xid={} from superior server", xid);
            }
            dispatcher.dispatch(new XAPlusRemoteSuperiorOrderToCommitEvent(xid));
            return true;
        } catch (IllegalArgumentException iae) {
            return false;
        } catch (InterruptedException ie) {
            return false;
        }
    }

    @RequestMapping("xaplus/rollback")
    boolean rollback(@RequestParam("xid") String xidString) {
        try {
            XAPlusXid xid = new XAPlusXid(xidString);
            if (logger.isDebugEnabled()) {
                logger.debug("Got rollback request for xid={} from superior server", xid);
            }
            dispatcher.dispatch(new XAPlusRemoteSuperiorOrderToRollbackEvent(xid));
            return true;
        } catch (IllegalArgumentException iae) {
            return false;
        } catch (InterruptedException ie) {
            return false;
        }
    }

    @RequestMapping("xaplus/failed")
    boolean failed(@RequestParam("xid") String xidString) {
        try {
            XAPlusXid xid = new XAPlusXid(xidString);
            if (logger.isDebugEnabled()) {
                logger.debug("Got failed status for xid={} from subordinate server", xid);
            }
            dispatcher.dispatch(new XAPlusRemoteSubordinateFailedEvent(xid));
            return true;
        } catch (IllegalArgumentException iae) {
            return false;
        } catch (InterruptedException ie) {
            return false;
        }
    }

    @RequestMapping("xaplus/done")
    boolean done(@RequestParam("xid") String xidString) {
        try {
            XAPlusXid xid = new XAPlusXid(xidString);
            if (logger.isDebugEnabled()) {
                logger.debug("Got done status for xid={} from subordinate server", xid);
            }
            dispatcher.dispatch(new XAPlusRemoteSubordinateDoneEvent(xid));
            return true;
        } catch (IllegalArgumentException iae) {
            return false;
        } catch (InterruptedException ie) {
            return false;
        }
    }

    @RequestMapping("xaplus/retry")
    boolean retry(@RequestParam("serverId") String serverId) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Got retry request from subordinate server={}", serverId);
            }
            dispatcher.dispatch(new XAPlusRemoteSubordinateRetryRequestEvent(serverId));
            return true;
        } catch (InterruptedException ie) {
            return false;
        }
    }
}