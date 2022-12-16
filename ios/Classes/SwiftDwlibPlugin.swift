import Flutter
import UIKit
import CoreData

public class SwiftDwlibPlugin: NSObject, FlutterPlugin {
    static var shared : SwiftDwlibPlugin!
    
    
    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: "dwlib", binaryMessenger: registrar.messenger())
        let instance = SwiftDwlibPlugin()
        registrar.addApplicationDelegate(instance)
        registrar.addMethodCallDelegate(instance, channel: channel)
        shared = instance
    }
    
    public func getAppDelegateContext() -> NSManagedObjectContext?{
        let appDelegate = UIApplication.shared.delegate as? SwiftDwlibPluginDelegate
        return appDelegate?.getContext()
    }
    
    public func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        
    }
    
    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        let _: UIViewController =
        (UIApplication.shared.delegate?.window??.rootViewController)!;
        switch(call.method){
        case "get_list":
            self.getList(result: result)
            break;
        case "start":
            self.start(result: result, call: call)
            break;
        case "pause":
            self.pause(result: result, call: call)
            break;
        case "resume":
            self.resume(result: result, call: call)
            break;
        case "cancel":
            self.cancel(result: result, call: call)
            break;
        case "delete":
            self.delete(result: result, call: call)
            break;
        case "retry":
            self.retry(result: result, call: call)
            break;
        default:
            print("method wasn't found : ",call.method);
            result("method wasn't found : "+call.method)
        }
    }
    
    func getList(result: @escaping FlutterResult){
        let allItems = DownloadManger.shared.allItems() ?? [[String: Any]]()
        result(self.json(from:allItems as Any))
    }
    
    
    func start(result: @escaping FlutterResult,call: FlutterMethodCall){
        guard let args = call.arguments else {
            return
        }
        if let myArgs = args as? [String: Any],
           let id : String = myArgs["id"] as? String,
           let url : String = myArgs["link"] as? String,
           let fileName : String = myArgs["fileName"] as? String,
           let whereToSave : String = myArgs["savedDir"] as? String{
            if self.getActiveCount() <= 5 {
                DownloadManger.shared.download(id: id, url: url, fileName: fileName,whereToSave: whereToSave)
                result(true)
            } else {
                result(false)
            }
            
        } else {
            print("iOS could not extract flutter arguments in method: (startDownload)")
            result(false)
        }
        result(false)
    }
    
    
    func pause(result: @escaping FlutterResult,call: FlutterMethodCall){
        guard let args = call.arguments else {
            return
        }
        if let myArgs = args as? [String: Any],
           let id : String = myArgs["id"] as? String
        {
            DownloadManger.shared.pause(id: id)
            result(true)
        } else {
            print("iOS could not extract flutter arguments in method: (pauseDownloadItem)")
            result(false)
        }
    }
    
    func resume(result: @escaping FlutterResult,call: FlutterMethodCall){
        guard let args = call.arguments else {
            return
        }
        if let myArgs = args as? [String: Any],
           let id : String = myArgs["id"] as? String,
           let whereToSave : String = myArgs["savedDir"] as? String{
            if self.getActiveCount() <= 5 {
                DownloadManger.shared.resume(id: id,whereToSave: whereToSave)
                result(true)
            } else {
                result(false)
            }
            
        } else {
            print("iOS could not extract flutter arguments in method: (resumeDownloadItem)")
            result(false)
        }
    }
    
    func cancel(result: @escaping FlutterResult,call: FlutterMethodCall){
        guard let args = call.arguments else {
            return
        }
        if let myArgs = args as? [String: Any],
           let id : String = myArgs["id"] as? String
        {
            DownloadManger.shared.stop(id: id)
            result(true)
        } else {
            print("iOS could not extract flutter arguments in method: (cancelDownloadItem)")
            result(false)
        }
    }
    
    func delete(result: @escaping FlutterResult,call: FlutterMethodCall){
        guard let args = call.arguments else {
            return
        }
        if let myArgs = args as? [String: Any],
           let id : String = myArgs["id"] as? String
        {
            DownloadManger.shared.delete(id: id)
            result(true)
        } else {
            print("iOS could not extract flutter arguments in method: (deleteDownloadItem)")
            result(false)
        }
        result(false)
    }
    
    func retry(result: @escaping FlutterResult,call: FlutterMethodCall){
        guard let args = call.arguments else {
            return
        }
        if let myArgs = args as? [String: Any],
           let id : String = myArgs["id"] as? String,
           let whereToSave : String = myArgs["savedDir"] as? String
        {
            if self.getActiveCount() <= 5 {
                DownloadManger.shared.retry(id: id, whereToSave: whereToSave)
                result(true)
            } else {
                result(false)
            }
            result(true)
        } else {
            print("iOS could not extract flutter arguments in method: (retryDownloadItem)")
            result(false)
        }
        result(false)
    }
    
    func json(from object:Any) -> String? {
        guard let data = try? JSONSerialization.data(withJSONObject: object, options: []) else {
            return nil
        }
        return String(data: data, encoding: String.Encoding.utf8)
    }
    
    func saveContext () {
        guard let context = getAppDelegateContext() else { return }
        if context.hasChanges {
            do {
                try context.save()
            } catch {
                let nserror = error as NSError
                fatalError("Unresolved error \(nserror), \(nserror.userInfo)")
            }
        }
    }
    
    func getActiveCount() -> Int{
        let allItems = DownloadManger.shared.allDownloadItems() ?? [Download]()
        var activeCount = 0
        for item in allItems {
            if item.status == DownloadManger.DownloadStatus.active.rawValue {
                activeCount = activeCount + 1
            }
        }
        return activeCount
    }
}

public protocol SwiftDwlibPluginDelegate {
    func getContext()-> NSManagedObjectContext?
}
