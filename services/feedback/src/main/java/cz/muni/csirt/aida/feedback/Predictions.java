package cz.muni.csirt.aida.feedback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import cz.muni.csirt.aida.idea.Idea;
import cz.muni.csirt.aida.idea.aida.AidaUtils;
import cz.muni.csirt.aida.mining.model.Item;

public class Predictions {

	private Map<String, Map<Item, Map<String, Queue<Idea>>>> predictions = new HashMap<>();

	public void detect(Idea prediction) {
		String ip = AidaUtils.getSourceIP4(prediction);
		Map<Item, Map<String, Queue<Idea>>> itemMap = predictions.computeIfAbsent(ip, x -> new HashMap<>());

		Item item = new Item(prediction.getNode().get(0).getName(),
				prediction.getCategory().get(1), // first category is test in prediction events
				AidaUtils.getTargetPort(prediction));
		Map<String, Queue<Idea>> rulesMap = itemMap.computeIfAbsent(item, x -> new HashMap<>());

		String rule = prediction.getNote();
		Queue<Idea> listOfPredictions = rulesMap.computeIfAbsent(rule, x -> new LinkedList<>());

		listOfPredictions.add(prediction);
	}

	public List<Idea> getPredictionsFor(Idea idea) {

		String ip = AidaUtils.getSourceIP4(idea);
		Map<Item, Map<String, Queue<Idea>>> itemMap = predictions.get(ip);
		if (itemMap == null) {
			return Collections.emptyList();
		}

		Item item = new Item(
				idea.getNode().get(0).getName(),
				idea.getCategory().get(0),
				AidaUtils.getTargetPort(idea));
		Map<String, Queue<Idea>> ruleMap = itemMap.get(item);
		if (ruleMap == null) {
			return Collections.emptyList();
		}

		List<Idea> predictionsForAlert = new ArrayList<>();
		for (Iterator<Map.Entry<String, Queue<Idea>>> it = ruleMap.entrySet().iterator(); it.hasNext();) {
			Queue<Idea> listOfPredictions = it.next().getValue();

			Idea prediction = listOfPredictions.poll();

			if (listOfPredictions.isEmpty()) {
				it.remove();
			}

			long mitigationTime = dateDiff(prediction.getDetectTime(), idea.getDetectTime(), TimeUnit.SECONDS);
			prediction.setAdditionalProperty("mitigationTime", (int) mitigationTime);

			predictionsForAlert.add(prediction);
		}

		if (ruleMap.isEmpty()) {
			itemMap.remove(item);
			if (itemMap.isEmpty()) {
				predictions.remove(ip);
			}
		}

		return predictionsForAlert;
	}

	private static long dateDiff(Date date1, Date date2, TimeUnit timeUnit) {
		long diffInMillies = date2.getTime() - date1.getTime();
		return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
	}
}
