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

    XAPlusRestController(XAPlus xaPlus) {
        this.dispatcher = xaPlus.dispatcher;
    }

    @RequestMapping("xaplus/ready")
    boolean ready(@RequestParam("xid") String xidString) {
        try {
            XAPlusXid xid = XAPlusXid.fromString(xidString);
            if (logger.isDebugEnabled()) {
                logger.debug("Got ready status for xid={} from subordinate server", xid);
            }
            dispatcher.dispatch(new XAPlusRemoteSubordinateReadyEvent(xid));
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
            XAPlusXid xid = XAPlusXid.fromString(xidString);
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
            XAPlusXid xid = XAPlusXid.fromString(xidString);
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
            XAPlusXid xid = XAPlusXid.fromString(xidString);
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

    @RequestMapping("xaplus/retry")
    boolean retry(@RequestParam("xid") String xidString) {
        try {
            XAPlusXid xid = XAPlusXid.fromString(xidString);
            if (logger.isDebugEnabled()) {
                logger.debug("Got retry request from subordinate server, xid={}", xid);
            }
            dispatcher.dispatch(new XAPlusRemoteSubordinateRetryRequestEvent(xid));
            return true;
        } catch (InterruptedException ie) {
            return false;
        }
    }
}
