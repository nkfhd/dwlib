import Flutter
import UIKit
import CoreData

public class SwiftDwlibPlugin: NSObject, FlutterPlugin {
    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: "dwlib", binaryMessenger: registrar.messenger())
        let instance = SwiftDwlibPlugin()
        registrar.addApplicationDelegate(instance)
        registrar.addMethodCallDelegate(instance, channel: channel)
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
        default:
            print("method wasn't found : ",call.method);
            result("method wasn't found : "+call.method)
        }
    }
    
    func getList(result: @escaping FlutterResult){
        let allItems = DownloadManger.shared.allItems() ?? [[String: Any]]()
        result(self.json(from:allItems as Any))
    }
    
    
    func json(from object:Any) -> String? {
        guard let data = try? JSONSerialization.data(withJSONObject: object, options: []) else {
            return nil
        }
        return String(data: data, encoding: String.Encoding.utf8)
    }
    
    lazy var persistentContainer: NSPersistentContainer = {
        let container = NSPersistentContainer(name: Constants.CoreData.containerName)
        container.loadPersistentStores(completionHandler: { (storeDescription, error) in
            if let error = error as NSError? {
                fatalError("Unresolved error \(error), \(error.userInfo)")
            }
        })
        return container
    }()
    
    func saveContext () {
        let context = persistentContainer.viewContext
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
