package com.rhino.foscam.accessor;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.rhino.foscam.exception.GeneralException;
import com.rhino.foscam.pojo.hd.DevInfo;
import com.rhino.foscam.pojo.hd.FTP;
import com.rhino.foscam.pojo.hd.HDLog;
import com.rhino.foscam.pojo.hd.SystemTime;

public class XMLUtils {

	private static Document parseXml(String xml) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xml));
		return builder.parse(is);
	}
	
	public static HDLog parseLog(String logXml, HDLog log, int offset) throws Exception {
		Document doc = parseXml(logXml);
		Node parent = doc.getFirstChild();
		NodeList nodes = parent.getChildNodes();
		
		int totalCount = 0;
		
		int index = 0;
		while(nodes.item(index) != null) {
			Node currentNode = nodes.item(index);
			String name = currentNode.getNodeName();
			String content = currentNode.getTextContent();
			index++;
			
			if(name.equals("result") && content.equals("-1")) {
				throw new GeneralException();
			}
			
			if(name.contains("log") && !content.isEmpty()) {
				log.updateLog(content);
			} else if(name.equals("totalCnt")) {
				totalCount = Integer.parseInt(content);
			}
		}
		
		if(totalCount <= offset + 10) {
			log.setComplete(true);
		}
		return log;
	}
	
	public static DevInfo parseDevInfo(String xml) throws Exception {
		Document doc = parseXml(xml);
		Node parent = doc.getFirstChild();
		NodeList nodes = parent.getChildNodes();
		DevInfo devInfo = new DevInfo();

		int index = 0;
		while(nodes.item(index) != null) {
			Node currentNode = nodes.item(index);
			String name = currentNode.getNodeName();
			String content = currentNode.getTextContent();
			index++;

			if(name.equals("result")) {
				if(content.equals("-1")) {
					throw new GeneralException();
				}
			} else if(name.equals("devName")) {
				devInfo.setCameraName(content);
			} else if(name.equals("year")) {
				devInfo.setYear(Integer.parseInt(content));
			} else if(name.equals("mon")) {
				devInfo.setMonth(Integer.parseInt(content));
			} else if(name.equals("day")) {
				devInfo.setDay(Integer.parseInt(content));
			} else if(name.equals("hour")) {
				devInfo.setHour(Integer.parseInt(content));
			} else if(name.equals("min")) {
				devInfo.setMinute(Integer.parseInt(content));
			} else if(name.equals("sec")) {
				devInfo.setSecond(Integer.parseInt(content));
			} else if(name.equals("timeZone")) {
				devInfo.setTimeZone(Integer.parseInt(content));
			}
		}
		return devInfo;
	}

    public static SystemTime parseSystemTime(String xml) throws Exception {
        Document doc = parseXml(xml);
        Node parent = doc.getFirstChild();
        NodeList nodes = parent.getChildNodes();
        SystemTime systemTime = new SystemTime();

        int index = 0;
        while(nodes.item(index) != null) {
            Node currentNode = nodes.item(index);
            String name = currentNode.getNodeName();
            String content = currentNode.getTextContent();
            index++;

            if(name.equals("result")) {
                if(content.equals("-1")) {
                    throw new GeneralException();
                }
            } else if(name.equals("timeSource")) {
                systemTime.setTimeSource(content);
            } else if(name.equals("ntpServer")) {
                systemTime.setNtpServer(content);
            } else if(name.equals("dateFormat")) {
                systemTime.setDateFormat(content);
            } else if(name.equals("timeFormat")) {
                systemTime.setTimeFormat(content);
            } else if(name.equals("timeZone")) {
                systemTime.setTimeZone(content);
            } else if(name.equals("isDst")) {
                systemTime.setIsDst(content);
            } else if(name.equals("year")) {
                systemTime.setYear(content);
            } else if(name.equals("mon")) {
                systemTime.setMonth(content);
            } else if(name.equals("day")) {
                systemTime.setDay(content);
            } else if(name.equals("hour")) {
                systemTime.setHour(content);
            } else if(name.equals("minute")) {
                systemTime.setMin(content);
            }
        }
        return systemTime;
    }

    public static FTP parseFTP(String xml) throws Exception{
        Document doc = parseXml(xml);
        Node parent = doc.getFirstChild();
        NodeList nodes = parent.getChildNodes();
        FTP ftp = new FTP();

        int index = 0;
        while(nodes.item(index) != null) {
            Node currentNode = nodes.item(index);
            String name = currentNode.getNodeName();
            String content = currentNode.getTextContent();
            index++;

            if(name.equals("result")) {
                if(content.equals("-1")) {
                    throw new GeneralException();
                }
            } else if(name.equals("ftpAddr")) {
                ftp.setFtpAddr(content);
            } else if(name.equals("ftpPort")) {
                ftp.setFtpPort(content);
            } else if(name.equals("mode")) {
                ftp.setMode(content);
            } else if(name.equals("userName")) {
                ftp.setUserName(content);
            } else if(name.equals("password")) {
                ftp.setPassword(content);
            }
        }
        return ftp;
    }

    public static String parseFTPTest(String xml) throws Exception{
        Document doc = parseXml(xml);
        Node parent = doc.getFirstChild();
        NodeList nodes = parent.getChildNodes();
        String result = "0";

        int index = 0;
        while(nodes.item(index) != null) {
            Node currentNode = nodes.item(index);
            String name = currentNode.getNodeName();
            String content = currentNode.getTextContent();
            index++;

            if(name.equals("result")) {
                if(content.equals("-1")) {
                    throw new GeneralException();
                }
            } else if(name.equals("testResult")) {
                result = content;
            }
        }
        return result;
    }
}
