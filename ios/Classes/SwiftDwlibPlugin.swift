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
        let flutterViewController: UIViewController =
        (UIApplication.shared.delegate?.window??.rootViewController)!;
        switch(call.method){
        case "get_list":
            self.getList(result: result)
            break;
        case "start":
            self.start(result: result, call: call)
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
        DownloadManger.shared.download(id: "111", url: "https://thekee.gcdn.co/video/m-159n/English/Animation&Family/Baby.Shark.Best.Kids.Song/S01/01.mp4", fileName: "test.mp4",whereToSave: "77810/205728",result: result)
        result(true)
        //        guard let args = call.arguments else {
        //            return
        //        }
        //        if let myArgs = args as? [String: Any],
        //           let id : String = myArgs["id"] as? String,
        //           let url : String = myArgs["link"] as? String,
        //           let fileName : String = myArgs["fileName"] as? String,
        //           let whereToSave : String = myArgs["savedDir"] as? String{
        //            if self.getActiveCount() <= 5 {
        //                DownloadManger.shared.download(id: id, url: url, fileName: fileName,whereToSave: whereToSave)
        //                result(true)
        //            } else {
        //                result(false)
        //            }
        //
        //        } else {
        //            print("iOS could not extract flutter arguments in method: (startDownload)")
        //            result(false)
        //        }
        //        result(false)
    }
    
    
    func json(from object:Any) -> String? {
        guard let data = try? JSONSerialization.data(withJSONObject: object, options: []) else {
            return nil
        }
        return String(data: data, encoding: String.Encoding.utf8)
    }
    
    
    //     lazy var persistentContainer: NSPersistentContainer = {
    //         let container = NSPersistentContainer(name: Constants.CoreData.containerName)
    //         container.loadPersistentStores(completionHandler: { (storeDescription, error) in
    //             if let error = error as NSError? {
    //                 fatalError("Unresolved error \(error), \(error.userInfo)")
    //             }
    //         })
    //         return container
    //     }()
    
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
    
}

public protocol SwiftDwlibPluginDelegate {
    func getContext()-> NSManagedObjectContext?
}
