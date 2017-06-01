package simulator.queueing.event;

import simulator.*;
import simulator.queueing.*;
import simulator.queueing.object.*;

public class Switch extends Event {
	
	private Queue queue;
	private Server server;
	
	public Switch(double date_, Server server_ , Queue queue_) {
		super(date_);
		this.queue = queue_;
		this.server = server_;
	}
	
	

	@Override
	public void execute(DES simu) {
		// TODO Auto-generated method stub
		//((SimpleSwitchServer)server).processSwitch( (QDES) simu, this.queue);
		((BatchSwitchServer)server).processSwitch( (QDES) simu, this.queue);
		
	}

}
